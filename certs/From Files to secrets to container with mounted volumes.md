### How to populate the keystores and truststores, trustanchor files in k8s cluster  
A general approach how to secrets are mounted volumes can be found in the official [documentation](https://kubernetes.io/docs/tasks/inject-data-application/distribute-credentials-secure/#create-a-pod-that-has-access-to-the-secret-data-through-a-volume)  
1.) generate the keystore, truststore trust_anchor  as described in [PlaceYourGatewayAccessKeysHere.md](PlaceYourGatewayAccessKeysHere.md)  
2.) combine the resulting files in a single secret with  
```(bash)
kubectl create secret generic combined_tls_secret --dry-run=client -o yaml --from-file=tls_key_store.p12 --from-file=tng_tls_server_truststore.p12 --from-file=trustanchor_store.jks > combined_tls_secret.yaml
kubectl create secret generic <secret-name> --dry-run=client -o yaml --from-file=<file1.p12> --from-file=<file2>.p12 --from-file=<file3.jks> > combined_tls_secret.yaml
```
this will result in a yaml file containing the base64 encoded file contents of that three files
```(json)
apiVersion: v1
data:
  file1.p12: MIIF3wIBAzCCBZUGDQEJFDEkHiIAYwBsAGkAZQBuAHQAYwByAGUAZABlAG4AdABpAGEAbABzMEEwMTANBglghkgBZQMEAgEFAAQgt/aPlSTVrkAIplPg++vrX...../czGzdjH1XPrutiae8EAFoECKv4c1pYD2TDAgIIAA==
  file2.p12: /u3+7QAAAAIAAAABAAAAAgAadG5nLXRscy1zZXJ2ZXItY2VydGlmaWNhdGUAAAGLVC9h5gAFWC41MDkAAAUaMIIFFjCCAv6gAwIBAgIRAJErCEr
  file3.jks: /u3+7QAAAAIAAAABAAAAAgAXoB1.....lBMEKIq4QDUOXoRgffuDghje1WrG9ML+Hbisq/yFOGwXD9RiX8F6sw6W4avAuvDsz
kind: Secret
metadata:
  creationTimestamp: null
  name: combined_tls_secret
```
This file then can be temporarily included in your helm charts or directly applied to your cluster with
```(shell)
kubectl apply -f combined_tls_secret.yaml # will apply the secret to current context
```  
**Note that your secrets with keystores/truststores contain sensible data. Keep them in save place**

In the deployment of your helm chart include the the secret as volumes in the template spec
````(helm)
spec:
  template:
    spec:
      volumes:
        - name: secrets-jks
          secret:
            secretName: mtls-secret
            items:
              - key: file1.p12
                path: tls_key_store.p12
              - key: file2.jks
                path: trustanchor_store.jks
              - key: file3.p12
                path: tng_tls_server_truststore.p12
````
<!-- TODO: check if the population is done automatically following this description -->
**the population is done automatically following this description**  
*items: io.k8s.api.core.v1.KeyToPath[]  
  defined in: Kubernetes v1.28.x (embedded)  
items If unspecified, each key-value pair in 
the Data field of the referenced Secret will be projected into the volume as a file whose name is the key and content is the value. If specified, the listed keys will be projected into the specified paths, and unlisted keys will not be present. If a key is specified which is not present in the Secret, the volume setup will error unless it is marked optional. Paths must be relative and may not contain the '..' path or start with '..'.*


The according volume mounts are defined in the container section
````(helm)
spec:
  templates:
    spec:
      containers:
        volumeMounts:
          - name: secrets-jks
            mountPath: /certs
            readOnly: true
````


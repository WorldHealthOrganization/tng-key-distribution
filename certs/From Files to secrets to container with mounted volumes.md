### How to populate the keystores and truststores, trustanchor files in k8s cluster  
A general approach how to secrets are mounted volumes can be found in the official [documentation](https://kubernetes.io/docs/tasks/inject-data-application/distribute-credentials-secure/#create-a-pod-that-has-access-to-the-secret-data-through-a-volume)  
1.) generate the keystore, truststore trust_anchor  as described in [PlaceYourGatewayAccessKeysHere.md](PlaceYourGatewayAccessKeysHere.md)  
2.) combine the resulting files in a single secret with  
```(bash)
kubectl create secret generic mtls-secret --dry-run=client -o yaml --from-file=tls_key_store.p12 --from-file=tng_tls_server_truststore.p12 --from-file=trustanchor_store.jks > mtls_secret.yaml
kubectl create secret generic <secret-name> --dry-run=client -o yaml --from-file=<file1.p12> --from-file=<file2>.p12 --from-file=<file3.jks> > combined_tls_secret.yaml
```
this will result in a yaml file containing the base64 encoded file contents of that three files
```(json)
apiVersion: v1
data:
  tls_key_store.p12: MIIF3wIBAzCCBZUGDQEJFDEkHiIAYwBsAGkAZQBuAHQAYwByAGUAZABlAG4AdABpAGEAbABzMEEwMTANBglghkgBZQMEAgEFAAQgt/aPlSTVrkAIplPg++vrX...../czGzdjH1XPrutiae8EAFoECKv4c1pYD2TDAgIIAA==
  trustanchor_store.jks: /u3+7QAAAAIAAAABAAAAAgAadG5nLXRscy1zZXJ2ZXItY2VydGlmaWNhdGUAAAGLVC9h5gAFWC41MDkAAAUaMIIFFjCCAv6gAwIBAgIRAJErCEr
  tng_tls_server_truststore.p12: /u3+7QAAAAIAAAABAAAAAgAXoB1.....lBMEKIq4QDUOXoRgffuDghje1WrG9ML+Hbisq/yFOGwXD9RiX8F6sw6W4avAuvDsz
kind: Secret
metadata:
  creationTimestamp: null
  name: mtls-secret
```
This file then can be temporarily included in your helm charts or directly applied to your cluster with
```(shell)
kubectl apply -f mtls-secret.yaml # will apply the secret to current context
```  
**Note that your secrets with keystores/truststores contain sensible data. Keep them in save place**

In the deployment of your helm chart include the secret as volumes in the template spec
````(helm)
spec:
  template:
    spec:
      volumes:
        - name: secrets-jks
          secret:
            secretName: mtls-secret
            items:
              - key: tls_key_store.p12
                path: tls_key_store.p12
              - key: trustanchor_store.jks
                path: trustanchor_store.jks
              - key: tng_tls_server_truststore.p12
                path: tng_tls_server_truststore.p12
````
The items array is optional as long as the keynames reflect the filenames and all keys in the secret
shall be mapped to files

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


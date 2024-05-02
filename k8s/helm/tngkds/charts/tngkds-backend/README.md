
# tngkds-backend

![Version: 0.1.0](https://img.shields.io/badge/Version-0.1.0-informational?style=flat-square) ![Type: application](https://img.shields.io/badge/Type-application-informational?style=flat-square) ![AppVersion: 1.16.0](https://img.shields.io/badge/AppVersion-1.16.0-informational?style=flat-square)

A Helm chart for TNG Key Distribution Service

## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| affinity | object | `{}` |  |
| autoscaling.enabled | bool | `false` |  |
| autoscaling.maxReplicas | int | `100` |  |
| autoscaling.minReplicas | int | `1` |  |
| autoscaling.targetCPUUtilizationPercentage | int | `80` |  |
| did.cron | string | `"*/10 * * * * *"` | spring cronjob configuration, how often shall the did file be generated |
| did.didUploadProvider | string | `"local-file"` | Upload provider for Did document, currently local-file |
| did.localFile.directory | string | `"trustlist"` | If upload provider is local-file: directory of the generated file |
| did.localFile.file-name | string | `"did.json"` | If upload provider is local-file: file-name of the generated file  |
| did.did_controller | string | `"did:web:def"` | The controller that is generating the did.json / controlling its contents |
| did.did_id | string | `"did:web:abc"` | The ID of the did entry |
| did.enableDidGeneration | bool | `true` | Shall the did documents be generated |
| did.ld_proof_nonce | string | `"n0nc3"` | Nonce of the Did Document |
| did.ld_proof_verification_method | string | `"did:web:dummy.net"` | Verification Method of the DID Signer. Usually a did-link to a did.json containing the public key material that was used to sign this DID |
| did.trust_list_controller_prefix | string | `"did:web:abc"` |  |
| did.trust_list_id_prefix | string | `"did:web:abc"` |  |
| fullnameOverride | string | `""` |  |
| gateway.connector.enabled | bool | `true` |  |
| gateway.connector.endpoint | string | `"<endpoint of the tng>"` |  |
| gateway.connector.max-cache-age | int | `300` |  |
| gateway.connector.proxy.enabled | bool | `false` | used for development, when your machine needs a proxy to access _tng.who.int_ |
| gateway.connector.tls_key_store.alias | string | `"<alias of the cert in keystore>"` | KDS application accesses the cert via its alias |
| gateway.connector.tls_key_store.password | string | `"<password to open keystore>"` |  |
| gateway.connector.tls_key_store.path | string | `"<full path of the keystore file>"` |  |
| gateway.connector.tls_trust_store.alias | string | `"<alias of the cert in truststore>"` |KDS application accesses the cert via its alias |
| gateway.connector.tls_trust_store.password | string | `"<password to open truststore>"` |  |
| gateway.connector.tls_trust_store.path | string | `"<full pathname of the truststore file>"` |  |
| gateway.connector.trust_anchor.alias | string | `"<alias of the trust_anchor chert>"` | tng application access the cert via its alias |
| gateway.connector.trust_anchor.password | string | `"<password to open trust_anchor_store>"` |  |
| gateway.connector.trust_anchor.path | string | `"<full path of the trust_anchor_store>"` |  |
| image.pullPolicy | string | `"IfNotPresent"` |  |
| image.repository | string | `"ghcr.io/worldhealthorganization/tng-key-distribution/tng-key-distribution"` |  |
| image.tag | string | `"0.0.1-d890889"` | version of the container image to be used for deployment |
| imagePullSecrets | string | `"tng-distribution-pull-secret"` |  |
| ingress.annotations | object | `{}` |  |
| ingress.className | string | `""` |  |
| ingress.enabled | bool | `false` |  |
| ingress.hosts[0].host | string | `"chart-example.local"` |  |
| ingress.hosts[0].paths[0].path | string | `"/"` |  |
| ingress.hosts[0].paths[0].pathType | string | `"ImplementationSpecific"` |  |
| ingress.tls | list | `[]` |  |
| liquibaseImage.repository | string | `"ghcr.io/worldhealthorganization/tng-key-distribution/tng-key-distribution-initcontainer"` |  |
| liquibaseImage.tag | string | `"<liquibase-image-tag>"` | version of the initcontainer image to be used, the tag is the same as for _image.tag_ |
| nameOverride | string | `""` |  |
| nodeSelector | object | `{}` |  |
| podAnnotations | object | `{}` |  |
| podSecurityContext | object | `{}` |  |
| psql.cluster | string | `"svc.cluster.local"` |  |
| psql.dbName | string | `"postgres"` | Name of the Shema to be used |
| psql.password | string | `"<dbpassword>"` | Password of the _psql.username_ |
| psql.port | int | `5432` | port where the db service is running |
| psql.serviceName | string | `"postgres"` | Name of the db service |
| psql.username | string | `"<dbusername>"` | user that ist used to perform the liquibase actions and to r/w to the DB |
| replicaCount | int | `1` |  |
| resources | object | `{}` |  |
| securityContext | object | `{}` |  |
| server.port | int | `8080` | port of the kds applications api server |
| service.ports[0].name | string | `"http"` |  |
| service.ports[0].nodePort | int | `30166` |  |
| service.ports[0].port | int | `8080` |  |
| service.ports[0].protocol | string | `"TCP"` |  |
| service.ports[0].targetPort | int | `8080` |  |
| service.type | string | `"NodePort"` |  |
| serviceAccount.annotations | object | `{}` |  |
| serviceAccount.create | bool | `true` |  |
| serviceAccount.name | string | `""` |  |
| spring.profile | string | `"cloud"` | {_0..n_} Spring profiles to be activated, usually used for feature toggle, currently not in use (existing values will be ignored) |
| tolerations | list | `[]` |  |


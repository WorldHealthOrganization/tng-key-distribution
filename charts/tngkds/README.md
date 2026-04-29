
# KDS Helm Chart

![Version: 0.1.0](https://img.shields.io/badge/Version-0.1.0-informational?style=flat-square) ![Type: application](https://img.shields.io/badge/Type-application-informational?style=flat-square) ![AppVersion: 1.16.0](https://img.shields.io/badge/AppVersion-1.16.0-informational?style=flat-square)

# The Helm chart for TNG Key Distribution Service.
## Introduction
This Helm chart simplifies deploying and configuring the TNG Key Distribution Service. It packages the container image, defines deployment settings (such as replicas, resource limits, and autoscaling), and allows customization of environment variables for aspects like the datasource, DID document generation, and gateway connector configuration. It also provides parameters for setting up secrets, ingress, and other deployment details, ensuring that the application can be tailored easily to different environments.


### Values: configuration parameters for the KDS deployment explained
This values are specific for the Service configuration. General values for deployment (like replicaCount, resources, ingress, etc.) are explained in the [general kubernetes documentation](https://kubernetes.io/docs/home/).

The keys should be exported as environment variables to the container, by replacing `.` with `_` and capitalizing all letters. E.g. `did.enableDidGeneration` becomes `DID_ENABLEDIDGENERATION`.  

**When a default value is suitable for your deployment, you do not have to provide/override it.**

| Key     |  Type  | Default | Description |
|---------|:------:|:---:|-------------|
| dgc.did.enableDidGeneration |  bool  |                                                                                                               | Shall the did documents be generated |
| dgc.did.cron | string |                                                `"0 0 2 * * *"`                                                | Spring cronjob configuration, how often shall the did files be generated<br/>(cf: https://docs.spring.io/spring-framework/reference/integration/scheduling.html#scheduling-cron-expression) |
| dgc.did.localkeystore.path | string |                                            `/cert/did-signer.p12`                                             | The directory where the did-signer keystore<>is located within the container. |
| dgc.did.localkeystore.alias | string |                                                `"did-signer"`                                                 | The alias of the private key in the did-signer keystore |
| dgc.did.localkeystore.password | string |                                              `"secure-password"`                                              | The password to open the did-signer keystore. |
| dgc.did.didUploadProvider | string |                                           `"local-file" \| "dummy"`                                           | Upload provider for Did document, currently local-file: git upload, dummy: for unit tests |
| dgc.did.localFile.directory | string |                                                 `"trustlist"`                                                 | If upload provider is local-file: root directory of the generated file(s) in local file-system |
| dgc.did.localFile.file-name | string |                                                 `"did.json"`                                                  | If upload provider is local-file: file-name of the generated file(s) in local file-system |
| dgc.did.did_controller | string |                                      `"did:web:tng-cdn-dev.who.int:v2"`                                       | The controller that is generating the did.json / controlling its contents. This value must correspond with the url where the documents are finally stored, otherwise the did resolution will not work |
| dgc.did.did_id  | string |                                       `did:web:tng-cdn-dev.who.int:v2`                                        | The prefix/document root for the trustlist in DID Web notation (example resolves to https://tng-cdn.dev.who.int/v2/did.json) This value must correspond with the url where the documents are finally stored, otherwise the did resolution will not work |
| dgc.did.ld_proof_verification_method | string | did:web:raw.githubusercontent.com:<br/>WorldHealthOrganization:tng-participants-dev:<br/>main:WHO:signing:DID | Verification Method of the DID Signer. Usually a did-web link to a did.json containing the public key material that was used to sign this DID |
| dgc.did.workdir | string |                                            `"/tmp/kdsgituploader"`                                            | local folder used for checkout and update git repository |
| dgc.did.prefix | string |                                                    `"v2"`                                                     | prefix used as root folder name for generated files. The trustlist exported as DIDs is considered to be the version 2 (v2) of the trustlist spec (https://worldhealthorganization.github.io/smart-trust/concepts_did_gdhcn.html#did-trustlist-v2) |
| dgc.did.url  | string |                        `https://github.com/`<br/>`WorldHealthOrganization/tng-cdn-dev`                        | the git repository to work in |
| dgc.did.pat  | string |                                      `git did pat by secret tng-bot-dev`                                      | the personal access token of the technical user that has permission to write to the repository |
| dgc.did.didSigningProvider | string |                             `dummy`, `local-keystore` for configured private key                              | signing provider to be used to sign the did documents (proof section). dummy can be used for dev. "local-keystore"` should be used with configured private key in keystore. (see: How to setup signing material for DID Signing in [certs documentation](../../../../../certs/PlaceYourGatewayAccessKeysHere.md) |
| did.trust-list-path | string |                                                  `trustlist`                                                  | path that contains DID documents of trustlist |
| dgc.did.trust-list-ref-path | string |                                                `trustlist-ref`                                                | path that contains DID documents with references only |
| dgc.did.virtualCountries |  map   |                     DGC_DID_VIRTUALCOUNTRIES_XA=XXA <br/> DGC_DID_VIRTUALCOUNTRIES_XO=XXO                     | Map of alpha2/alpha3 countries that do not belong to a real states and are therefor not covered by the iso-3166 list. This is used either for testing issues for participants who do not belong to an single state |
| dgc.did.group-deny-list | array  |                                           [AUTHENTICATION, UPLOAD]                                            | List of certificate groups that will not be exported in the trustlist |
| dgc.did.group-name-mapping |  map   |                                               DGC_DID_CSCA=SCA                                                | Mapping of certificate group names used in the trustlist. Key: group name used in the TNG, Value: group name as used in KDS |
| dgc.gateway.connector.enabled |  bool  |                                                    `true`                                                     | This switch enables/disables the download of key material from the TNG. (For local unit testing purposes only) |
| dgc.gateway.connector.endpoint | string |                                           `"<endpoint of the tng>"`                                           | Url of the TNG |
| dgc.gateway.connector.max-cache-age |  int   |                                                     `300`                                                     | Timespan in sec. after which the key material is updated from the gateway. |
| dgc.gateway.connector.proxy.enabled |  bool  |                                                    `false`                                                    | Used for development, when your machine needs a proxy to access _tng.who.int_ |
| dgc.gateway.connector.tls_key_store.alias | string |                                      `"<alias of the cert in keystore>"`                                      | KDS application accesses the cert via its alias |
| dgc.gateway.connector.tls_key_store.password | string |                                        `"<password to open keystore>"`                                        |   |
| dgc.gateway.connector.tls_key_store.path | string |                                     `"<full path of the keystore file>"`                                      |   |
| dgc.gateway.connector.tls_trust_store.alias | string |                                     `"<alias of the cert in truststore>"`                                     | KDS application accesses the cert via its alias |
| dgc.gateway.connector.tls_trust_store.password | string |                                       `"<password to open truststore>"`                                       |   |
| dgc.gateway.connector.tls_trust_store.path | string |                                  `"<full pathname of the truststore file>"`                                   |   |
| dgc.gateway.connector.trust_anchor.alias | string |                                     `"<alias of the trust_anchor chert>"`                                     | tng application access the cert via its alias |
| dgc.gateway.connector.trust_anchor.password | string |                                   `"<password to open trust_anchor_store>"`                                   |    |
| dgc.gateway.connector.trust_anchor.path | string |                                   `"<full path of the trust_anchor_store>"`                                   |  |
| dgc.liquibaseImage.repository | string |      ghcr.io/worldhealthorganization/<br/>tng-key-distribution/<br/>tng-key-distribution-initcontainer`       | |
| dgc.liquibaseImage.tag | string |                                           `"<liquibase-image-tag>"`                                           | version of the initcontainer image to be used, the tag is the same as for _image.tag_ |
| spring.datasource.driverclassname | string |                                                `org.h2.Driver`                                                | The JDBC driver class|
| spring.jpa.databaseplatform | string |                                       `org.hibernate.dialect.H2Dialect`                                       | The Hibernate dialect |
| spring.datasource.jndi.name |  bool  |                                                    `false`                                                    | Is the database driver url exposed to the Java Naming and Directory interface |
| spring.datasource.url | string |                   `jdbc:h2:mem:dgc;`<br/>`DB_CLOSE_ON_EXIT=FALSE;`<br/>`DB_CLOSE_DELAY=-1;`                   | The JDBC URL for the database connection. Normally the storage does not need to be persistent, so the h2 in-memory db can be used. When you want a persistent storage, then adapt this URL with a db of you choice - currently there is an additional postgreSQL Driver compiled into the docker image, that could be used by only changing the DB URL. For additional Drivers you have to add further dependencies to the application's POM file and recompile it. |
| spring.datasource.username | string |                                                     `sa`                                                      | The username for the database connection |
| spring.datasource.password | string |                                                     `''`                                                      | The password for the database connection |
| server.port  |  int   |                                                    `8080`                                                     | port of the kds applications api server |
| secrets.didSigner | string |                                          `<DID signer certificate>`                                           | base64 encoded contents of the DID signer certificate |
| secrets.dockerPull | string |                                            `<docker pull secret>`                                             | base64 encoded docker pull secret |
| secrets.mtls.tlsKeyStore | string |                                               `<tls key store>`                                               | base64 encoded contents of the mTLS key store. This contains the client certificate for the mTLS connection. |
| secrets.mtls.tlsServerTrustStore | string |                                          `<tls server trust store>`                                           | base64 encoded contents of mTLS server trust store. This contains the CA(s) of TNGs TLS certificates. |
| secrets.mtls.tlsTrustAnchorStore | string |                                           `<tls trustanchor store>`                                           | base64 encoded contents of mTLS trustanchor store file. This contains the public key of the Trust Anchor. |

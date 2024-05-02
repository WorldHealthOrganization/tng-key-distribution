# tngkds

![Version: 0.1.0](https://img.shields.io/badge/Version-0.1.0-informational?style=flat-square) ![Type: application](https://img.shields.io/badge/Type-application-informational?style=flat-square) ![AppVersion: 1.16.0](https://img.shields.io/badge/AppVersion-1.16.0-informational?style=flat-square)

A Helm chart for Kubernetes

## Requirements
The versions from umbrella chart are currently not used, please refer to to corresponding image tags in value files 

| Repository | Name | Version |
|------------|------|---------|
|  | tngkds-backend | 0.1.0 |
|  | tngkds-postgres | 0.1.0 |

## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| tngkds-backend.gateway.connector.enabled | bool | `true` | flag whether the kds is connected to the TNG |
| tngkds-backend.gateway.connector.endpoint | string | `"<tng-gateway-endpoint>"` | The url where the TNG can be reached |
| tngkds-backend.gateway.connector.max-cache-age | int | `300` |  |
| tngkds-backend.gateway.connector.proxy.enabled | bool | `false` | used for development, when KDS is run behind a proxy. If set to true, _tngkds-backend.gateway.connector.proxy.port_ and _tngkds-backend.gateway.connector.proxy.host_ also need to be applied |
| tngkds-backend.gateway.connector.tls_key_store.alias | string | `"clientcredentials"` |  |
| tngkds-backend.gateway.connector.tls_key_store.password | string | `"<password of tls_key_store>"` |  |
| tngkds-backend.gateway.connector.tls_key_store.path | string | `"/certs/tls_key_store.p12"` |  |
| tngkds-backend.gateway.connector.tls_trust_store.alias | string | `"tng-tls-server-certificate"` |  |
| tngkds-backend.gateway.connector.tls_trust_store.password | string | `"<password of tls_truststore>"` |  |
| tngkds-backend.gateway.connector.tls_trust_store.path | string | `"/certs/tng_tls_server_truststore.p12"` |  |
| tngkds-backend.gateway.connector.trust_anchor.alias | string | `"trustanchor"` |  |
| tngkds-backend.gateway.connector.trust_anchor.password | string | `"<password of trustanchor_store>"` |  |
| tngkds-backend.gateway.connector.trust_anchor.path | string | `"/certs/trustanchor_store.jks"` |  |
| tngkds-backend.image.tag | string | `"<kds-image-tag>"` |  |
| tngkds-backend.liquibaseImage.tag | string | `"<liquibase-image-tag(initcontainer)>"` |  |
| tngkds-backend.path | string | `"/()(*)"` |  |
| tngkds-backend.port | int | `8080` |  |
| tngkds-backend.psql.asPod.enabled | bool | `false` |  |
| tngkds-backend.psql.cluster | string | `"svc.cluster.local"` |  |
| tngkds-backend.psql.dbName | string | `"kdsdb"` |  |
| tngkds-backend.psql.password | string | `nil` |  |
| tngkds-backend.psql.port | int | `5432` |  |
| tngkds-backend.psql.serviceName | string | `"postgresql-d01.postgres.database.azure.com"` |  |
| tngkds-backend.psql.username | string | `nil` |  |
| tngkds-postgres.asPod.enabled | bool | `false` |  |
| tngkds-postgres.path | string | `"/()(*)"` |  |
| tngkds-postgres.port | int | `5432` |  |

# How to setup signing material for DID Signing

KDS is able to provide a DID-Document holding the downloaded keys. The DID-Document will be signed by a private key provided in a KeyStore.

Generate Private Key (Choose another Curve depending your needs)

```
openssl ecparam -name prime256v1 -genkey -noout -out did-signer.pem
```

Convert PEM-File to KeyStore

```
openssl pkcs12 -export -out did-signer.p12 -inkey did-signer.pem -nocerts -passout pass:secure-password -name did-signer
```

This will result in a KeyStore (P12) containing the previously generated private key stored with alias "did-signer" and secured with password "secure-password"

```yaml
dgc:
  did:
    didSigningProvider: local-keystore
    localKeyStore:
      alias: did-signer
      password: secure-password
      path: ./certs/did-signer.p12
```

## How to publish corresponding public key for verification of DID signature

Generate the public key of the did singer

```
openssl ec -in did-signer.pem -pubout -out did-signer-public-key.pem
```

Adapt the following environment variables to your needs and generate a did document for your public key.

| Environment Variable | Description |
| --- | --- |
| `PUBLIC_KEY_FILE` | Path to the public key file (e.g., "./did-signer-public-key.pem") |
| `DID_ID` | Identifier for the DID (e.g., "did:web:raw.githubusercontent.com:WorldHealthOrganization:tng-participants-dev:main:WHO:signing:DID") |
| `DID_CONTROLLER` | Controller for the DID (e.g., "did:web:raw.githubusercontent.com:WorldHealthOrganization:tng-participants-dev:main:WHO:signing:DID") |

```
export PUBLIC_KEY_FILE="./did-signer-public-key.pem"
export DID_ID="did:web:raw.githubusercontent.com:WorldHealthOrganization:tng-participants-dev:main:WHO:signing:DID"
export DID_CONTROLLER="did:web:raw.githubusercontent.com:WorldHealthOrganization:tng-participants-dev:main:WHO:signing:DID"
python generate_did_document.py
```

Place the generated DID to it's intended location on a host corresponding to the DID ID as defined by [did:web method specification](https://w3c-ccg.github.io/did-method-web/).

## How to update the did-signer in the environment

```
kubectl create secret generic did-signer-secret --dry-run=client --namespace=kds -o yaml --from-file=did-signer.p12 > did-signer-secret.yaml
```

Connected to the correct kubernetes context deploy the generated secret

```(shell)
kubectl apply -f did-signer-secret.yaml
```

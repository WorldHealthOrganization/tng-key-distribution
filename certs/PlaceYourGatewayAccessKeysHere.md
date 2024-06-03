### Note: 

If you want to run the key distribution service via the given docker-compose file, place your keys to access the 
[DGCG](https://github.com/eu-digital-green-certificates/dgc-gateway) in this folder and adjust the file names
in the [docker-compose.yml](../docker-compose.yml) file.

Further information can be found in the [README](../README.md)

# How to use participants key material to access TNG

Copy the TLS.pem, TLS.key and CA.pem from your participant onboarding repository to this /certs folder.

Create a JKS TrustStore from the CA.pem:

R3 of the URL cert export (keystore explorer):

```
keytool -importcert -alias tng-tls-server-certificate -file R3.cer -keystore tng_tls_server_truststore.p12 -storepass dgcg-p4ssw0rd -storetype jks
```

Create a Trustanchor store from TNG TrustAnchor.pem:

```
keytool -importcert -alias trustanchor -file TA_CA.pem -keystore trustanchor_store.jks -storepass dgcg-p4ssw0rd -storetype jks
```

Create a pkcs12 KeyStore from the TLS.pem and TLS.key:

```
openssl pkcs12 -export -out tls_key_store.p12 -inkey TLS.key -in TLS.pem -passout pass:dgcg-p4ssw0rd -name clientcredentials
```

package tng.trustnetwork.keydistribution.client;

//import eu.europa.ec.dgc.gateway.connector.config.DgcGatewayConnectorConfigProperties;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;


@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty("kds.gateway.connector.enabled")
@Slf4j
public class KdsGatewayConnectorKeystore {

    private final KdsGatewayConnectorConfigProperties kdsConfigProperties;
    
    /**
     * Creates a KeyStore instance with keys for DGC TrustAnchor.
     *
     * @return KeyStore Instance
     * @throws KeyStoreException        if no implementation for the specified type found
     * @throws CertificateException     if any of the certificates in the keystore could not be loaded
     * @throws NoSuchAlgorithmException if the algorithm used to check the integrity of the keystore cannot be found
     */
    @Bean
    @Qualifier("trustAnchor")
    @ConditionalOnProperty("kds.gateway.connector.trust-anchor.path")
    public KeyStore trustAnchorKeyStore() throws KeyStoreException,
        CertificateException, NoSuchAlgorithmException {
        KeyStore keyStore = KeyStore.getInstance("JKS");

        loadKeyStore(
            keyStore,
            kdsConfigProperties.getTrustAnchor().getPath(),
            kdsConfigProperties.getTrustAnchor().getPassword());

        return keyStore;
    }

    /**
     * Creates a KeyStore instance with keys for TLS trust Store.
     *
     * @return KeyStore Instance
     * @throws KeyStoreException        if no implementation for the specified type found
     * @throws CertificateException     if any of the certificates in the keystore could not be loaded
     * @throws NoSuchAlgorithmException if the algorithm used to check the integrity of the keystore cannot be found
     */
    @Bean
    @Qualifier("tlsTrustStore")
    @ConditionalOnProperty("kds.gateway.connector.tls-trust-store.path")
    public KeyStore tlsTrustStore() throws KeyStoreException,
        CertificateException, NoSuchAlgorithmException {
        KeyStore keyStore = KeyStore.getInstance("JKS");

        loadKeyStore(
            keyStore,
            kdsConfigProperties.getTlsTrustStore().getPath(),
            kdsConfigProperties.getTlsTrustStore().getPassword());

        return keyStore;
    }


    /**
     * Creates a KeyStore instance with keys for TLS key Store.
     *
     * @return KeyStore Instance
     * @throws KeyStoreException        if no implementation for the specified type found
     * @throws CertificateException     if any of the certificates in the keystore could not be loaded
     * @throws NoSuchAlgorithmException if the algorithm used to check the integrity of the keystore cannot be found
     */
    @Bean
    @Qualifier("tlsKeyStore")
    @ConditionalOnProperty("kds.gateway.connector.tls-key-store.path")
    public KeyStore tlsKeyStore() throws KeyStoreException,
        CertificateException, NoSuchAlgorithmException {
        KeyStore keyStore = KeyStore.getInstance("JKS");

        loadKeyStore(
            keyStore,
            kdsConfigProperties.getTlsKeyStore().getPath(),
            kdsConfigProperties.getTlsKeyStore().getPassword());

        return keyStore;
    }


    private void loadKeyStore(KeyStore keyStore, String path, char[] password)
        throws CertificateException, NoSuchAlgorithmException {
        try {

            InputStream stream;

            if (path.startsWith("$ENV:")) {
                String env = path.substring(5);
                String b64 = System.getenv(env);
                stream = new ByteArrayInputStream(Base64.getDecoder().decode(b64));
            } else {
                stream = new FileInputStream(ResourceUtils.getFile(path));
            }

            if (stream.available() > 0) {
                keyStore.load(stream, password);
                stream.close();
            } else {
                keyStore.load(null);
                log.info("Could not load Keystore {}", path);
            }
        } catch (IOException e) {
            log.info("Could not load Keystore {}", path);
        }
    }
}

package tng.trustnetwork.keydistribution.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "kds.gateway.connector")
public class KdsGatewayConnectorConfigProperties {
    
    private boolean enabled = true;

    /**
     * Disables the ckeck of Uploader Certificate for downloaded DSC.
     */
    private boolean disableUploadCertificateCheck = false;

    /**
     * Endpoint of DGCG Service (without any path segments, e.g. https://example-dgcg.ec.europa.eu)
     */
    String endpoint;

    /**
     * Maximum Age of Trusted Certificates Cache in seconds.
     */
    private int maxCacheAge = 300;

    /**
     * Keystore containing the private Key and Certificate for mTLS connection to DGC Gateway.
     */
    private KeyStoreWithAlias tlsKeyStore;

    /**
     * Keystore containing the Truststore for mTLS connection to DGC Gateway.
     */
    private KeyStore tlsTrustStore;

    /**
     * Keystore containing the TrustAnchor Public Certificate.
     */
    private KeyStoreWithAlias trustAnchor;

    /**
     * Keystore containing the Upload Certificate.
     */
    private KeyStoreWithAlias uploadKeyStore;

    /**
     * Http-Proxy Configuration.
     */
    private Proxy proxy = new Proxy(true, null, -1);

    @Getter
    @Setter
    public static class KeyStoreWithAlias extends KeyStore {

        /**
         * Alias for the Certificate inside the Keystore which should be used.
         */
        private String alias;
    }

    @Getter
    @Setter
    public static class KeyStore {

        /**
         * Path to Keystore.
         * This can be an absolute path on local filesystem or embedded truststore (e.g. classpath:truststore.jks)
         */
        private String path;

        /**
         * Password for KeyStore.
         */
        private char[] password;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Proxy {

        public Proxy(boolean b, Object object, int i) {
			// TODO Auto-generated constructor stub
		}

		/**
         * Enable HTTP Proxy.
         */
        private boolean enabled;

        /**
         * Host Address of Proxy server (without protocol).
         * (e.g. proxy.example-corp.com)
         */
        private String host;

        /**
         * Port of Proxy Server.
         * (e.g. 8080)
         */
        private int port;
    }

}

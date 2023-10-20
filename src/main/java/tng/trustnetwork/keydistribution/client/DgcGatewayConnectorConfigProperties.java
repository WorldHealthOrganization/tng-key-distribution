package tng.trustnetwork.keydistribution.client;

/*-
 * ---license-start
 * EU Digital Green Certificate Gateway Service / dgc-lib
 * ---
 * Copyright (C) 2021 - 2022 T-Systems International GmbH and all other contributors
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ---license-end
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "dgc.gateway.connector")
public class DgcGatewayConnectorConfigProperties {

    private boolean enabled = false;

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
    private Proxy proxy = new Proxy(false, null, -1);

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


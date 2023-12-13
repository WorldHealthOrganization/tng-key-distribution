/*-
 * ---license-start
 * WorldHealthOrganization / tng-key-distribution
 * ---
 * Copyright (C) 2021 T-Systems International GmbH and all other contributors
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

package tng.trustnetwork.keydistribution.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("dgc") //TODO separate kds and dgc-lib properties
public class KdsConfigProperties {

    private final CertificatesDownloader certificatesDownloader = new CertificatesDownloader();

    private final TrustedIssuerDownloader trustedIssuerDownloader = new TrustedIssuerDownloader();

    private String context = "";

    /**
     * Http-Proxy Configuration.
     */
    private KdsConfigProperties.Proxy
        proxy = new KdsConfigProperties.Proxy(false, null, -1);

    @Getter
    @Setter
    public static class CertificatesDownloader {
        private Integer timeInterval;
        private Integer lockLimit;
    }

    @Getter
    @Setter
    public static class TrustedIssuerDownloader {
        private boolean enabled;
        private Integer timeInterval;
        private Integer lockLimit;
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

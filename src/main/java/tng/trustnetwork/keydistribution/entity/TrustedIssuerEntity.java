/*-
 * ---license-start
 * WorldHealthOrganization / tng-key-distribution
 * ---
 * Copyright (C) 2021 - 2024 T-Systems International GmbH and all other contributors
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

package tng.trustnetwork.keydistribution.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@Table(name = "trusted_issuer")
@AllArgsConstructor
@NoArgsConstructor
public class TrustedIssuerEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Timestamp of the Record.
     */
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    /**
     * ISO 3166 Alpha-2 Country Code
     * (plus code "EU" for administrative European Union entries).
     */
    @Column(name = "country", nullable = false, length = 2)
    private String country;

    /**
     * URL of the service, can be HTTP(s) or DID URL.
     */
    @Column(name = "url", nullable = false, length = 1024)
    private String url;

    /**
     * Name of the service.
     */
    @Column(name = "name", nullable = false, length = 512)
    private String name;

    /**
     * Type of the URL (HTTP, DID).
     */
    @Column(name = "url_type", nullable = false, length = 25)
    @Enumerated(EnumType.STRING)
    private UrlType urlType;

    /**
     * SHA-256 Thumbprint of the certificate (hex encoded).
     */
    @Column(name = "thumbprint", length = 64)
    private String thumbprint;

    /**
     * SSL Certificate of the endpoint (if applicable).
     */
    @Column(name = "ssl_public_key", length = 2048)
    private String sslPublicKey;

    /**
     * Type of Key Storage. E.g JWKS, DIDDocument etc. (If applicable)
     */
    @Column(name = "key_storage_type", length = 128)
    private String keyStorageType;

    /**
     * Signature of the TrustAnchor.
     */
    @Column(name = "signature", nullable = false, length = 6000)
    String signature;

    /**
     * The domain of the trustedIssuer.
     */
    @Column(name = "domain")
    private String domain;

    public enum UrlType {
        HTTP,
        DID
    }

}

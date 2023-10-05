package tng.trustnetwork.keydistribution.model;

import java.time.ZonedDateTime;

public class TrustedIssuer {
    private String url;
    private UrlType type;
    private String country;
    private String thumbprint;
    private String sslPublicKey;
    private String keyStorageType;
    private String signature;
    private ZonedDateTime timestamp;
    private String name;
    private String uuid;
    private String domain;

    public TrustedIssuer() {
    }

    public String getUrl() {
        return this.url;
    }

    public UrlType getType() {
        return this.type;
    }

    public String getCountry() {
        return this.country;
    }

    public String getThumbprint() {
        return this.thumbprint;
    }

    public String getSslPublicKey() {
        return this.sslPublicKey;
    }

    public String getKeyStorageType() {
        return this.keyStorageType;
    }

    public String getSignature() {
        return this.signature;
    }

    public ZonedDateTime getTimestamp() {
        return this.timestamp;
    }

    public String getName() {
        return this.name;
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public void setType(final UrlType type) {
        this.type = type;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public void setThumbprint(final String thumbprint) {
        this.thumbprint = thumbprint;
    }

    public void setSslPublicKey(final String sslPublicKey) {
        this.sslPublicKey = sslPublicKey;
    }

    public void setKeyStorageType(final String keyStorageType) {
        this.keyStorageType = keyStorageType;
    }

    public void setSignature(final String signature) {
        this.signature = signature;
    }

    public void setTimestamp(final ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public void setDomain(final String domain) {
        this.domain = domain;
    }

    public static enum UrlType {
        HTTP,
        DID;

        private UrlType() {
        }
    }
}

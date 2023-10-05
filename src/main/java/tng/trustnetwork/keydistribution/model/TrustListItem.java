package tng.trustnetwork.keydistribution.model;

import java.time.ZonedDateTime;

public class TrustListItem {
    private String kid;
    private ZonedDateTime timestamp;
    private String rawData;
    private String country;
    private String thumbprint;
    private String signature;

    public TrustListItem() {
    }

    public String getKid() {
        return this.kid;
    }

    public ZonedDateTime getTimestamp() {
        return this.timestamp;
    }

    public String getRawData() {
        return this.rawData;
    }

    public String getCountry() {
        return this.country;
    }

    public String getThumbprint() {
        return this.thumbprint;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setKid(final String kid) {
        this.kid = kid;
    }

    public void setTimestamp(final ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setRawData(final String rawData) {
        this.rawData = rawData;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public void setThumbprint(final String thumbprint) {
        this.thumbprint = thumbprint;
    }

    public void setSignature(final String signature) {
        this.signature = signature;
    }
}

package tng.trustnetwork.keydistribution.service;


public interface TrustedIssuerDownloadService {

    /**
     * Synchronises the trusted issuers with the gateway.
     */
    void downloadTrustedIssuers();
}

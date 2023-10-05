package tng.trustnetwork.keydistribution.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import tng.trustnetwork.keydistribution.model.TrustListItem;
import tng.trustnetwork.keydistribution.model.TrustedIssuer;

@ConditionalOnProperty({"dgc.gateway.connector.enabled"})
@Service
@EnableScheduling
@RequiredArgsConstructor
public class DummyDownloadConnector {

    private List<TrustListItem> trustedCertificates = new ArrayList();

    private List<TrustedIssuer> trustedIssuers = new ArrayList();

    public List<TrustListItem> getTrustedCertificates() {
        this.updateIfRequired();
        return Collections.unmodifiableList(this.trustedCertificates);
    }

    public List<TrustedIssuer> getTrustedIssuers() {
        this.updateIfRequired();
        return Collections.unmodifiableList(this.trustedIssuers);
    }

    private void updateIfRequired() {

    }
}

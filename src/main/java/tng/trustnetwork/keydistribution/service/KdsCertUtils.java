package tng.trustnetwork.keydistribution.service;

import eu.europa.ec.dgc.utils.CertificateUtils;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.cert.X509CertificateHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KdsCertUtils {

    private final CertificateUtils certificateUtils;

    /**
     * Parse Base64 Encoded Certificate.
     *
     * @param raw Base64 encoded certificate in DER format
     * @return parsed Certificate instance
     */
    public X509Certificate parseCertificate(String raw) {

        try {
            byte[] rawDataBytes = Base64.getDecoder().decode(raw);
            X509CertificateHolder certificateHolder = new X509CertificateHolder(rawDataBytes);
            return certificateUtils.convertCertificate(certificateHolder);
        } catch (CertificateException | IOException e) {
            return null;
        }
    }
}

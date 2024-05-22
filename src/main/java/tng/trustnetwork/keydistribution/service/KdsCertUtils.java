package tng.trustnetwork.keydistribution.service;

import eu.europa.ec.dgc.utils.CertificateUtils;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.cert.X509CertificateHolder;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class KdsCertUtils {

    private final CertificateUtils certificateUtils;

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

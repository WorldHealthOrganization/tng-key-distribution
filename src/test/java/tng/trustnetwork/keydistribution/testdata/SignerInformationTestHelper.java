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

package tng.trustnetwork.keydistribution.testdata;

import eu.europa.ec.dgc.gateway.connector.model.TrustListItem;
import eu.europa.ec.dgc.gateway.connector.model.TrustedCertificateTrustListItem;
import eu.europa.ec.dgc.utils.CertificateUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tng.trustnetwork.keydistribution.entity.SignerInformationEntity;
import tng.trustnetwork.keydistribution.repository.SignerInformationRepository;

@Service
@RequiredArgsConstructor
public class SignerInformationTestHelper {

    public static final String TEST_CERT_1_STR =
        "MIICrDCCAZSgAwIBAgIEYH+7ujANBgkqhkiG9w0BAQsFADAYMRYwFAYDVQQDDA1l"
            + "ZGdjX2Rldl90ZXN0MB4XDTIxMDQyMTA1NDQyNloXDTIyMDQyMTA1NDQyNlowGDEW"
            + "MBQGA1UEAwwNZWRnY19kZXZfdGVzdDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCC"
            + "AQoCggEBAOAlpphOE0TH2m+jU6prmP1W6N0ajaExs5X+sxxG58hIGnZchxFkLkeY"
            + "SZqyC2bPQtPiYIDgVFcPJPgfRO4r5ex3W7OxQCFS0TJmYhRkLiVQHQDNHeXFmOpu"
            + "834x2ErPJ8AK2D9KhVyFKl5OX1euU25IXzXs67vQf30eStArvWFlZGX4E+JUy8yI"
            + "wrR6WLRe+kgtBdFmJZJywbnnffg/5WT+TEcky8ugBlsEcyTxI5rt6iW5ptNUphui"
            + "8ZGaE2KtjcnZVaPCvn1IjEv6sdWS/DNDlFySuJ6LQD1OnKsjCXrNVZFVZS5ae9sn"
            + "Pu4Y/gapzdgeSDioRk6BWwZ02E9BE+8CAwEAATANBgkqhkiG9w0BAQsFAAOCAQEA"
            + "pE8H9uGtB6DuDL3LEqGslyJKyc6EBqJ+4hDlFtPe+13xEDomJsNwq1Uk3p9F1aHg"
            + "qqXc1MjJfDWn0l7ZDGh02tfi+EgHyV2vrfqZwXm6vuK/P7fzdb5blLJpKt0NoMCz"
            + "Y+lHhkCxcRGX1R8QOGuuGtnepDrtyeTuoQqsh0mdcMuFgKuTr3c3kKpoQwBWquG/"
            + "eZ0PhKSkqXy5aEaFAzdXBLq/dh4zn8FVx+STSpKK1WNmoqjtL7EEFcNgxLTjWJFj"
            + "usTEZL0Yxa4Ot4Gb6+VK7P34olH7pFcBFYfh6DyOESV9uglrE4kdOQ7+x+yS5zR/"
            + "UTeEfM4mW4I2QIEreUN8Jg==";

    public static final String TEST_CERT_1_KID = "8xYtW2837ac=";

    private final CertificateUtils certificateUtils;

    private X509Certificate convertStringToX509Cert(String certificate) throws CertificateException {
        InputStream targetStream = new ByteArrayInputStream(Base64.getDecoder().decode(certificate));
        return (X509Certificate) CertificateFactory
            .getInstance("X509")
            .generateCertificate(targetStream);
    }

    public TrustedCertificateTrustListItem createTrustedCertificateTrustListItem(String certStr) {
        String kid;
        try {
            kid = certificateUtils.getCertKid(convertStringToX509Cert(certStr));
        }catch (CertificateException e) {
            kid = "kid_"+ ZonedDateTime.now();
        }

        //TrustListItem item = new TrustListItem();
        TrustedCertificateTrustListItem item = new TrustedCertificateTrustListItem();
        item.setKid(kid);
        item.setCertificate(certStr);

        return item;
    }
}

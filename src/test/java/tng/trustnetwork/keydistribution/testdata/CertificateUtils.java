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

package tng.trustnetwork.keydistribution.testdata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Base64;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CertificateUtils {
    private static final Logger log = LoggerFactory.getLogger(CertificateUtils.class);
    private static final byte KID_BYTE_COUNT = 8;
    private final CertificateFactory certificateFactory = new CertificateFactory();

    public CertificateUtils() {
    }

    public String getCertKid(X509Certificate x509Certificate) {
        try {
            byte[] hashBytes = this.calculateHashBytes(x509Certificate.getEncoded());
            byte[] kidBytes = Arrays.copyOfRange(hashBytes, 0, 8);
            return Base64.getEncoder().encodeToString(kidBytes);
        } catch (NoSuchAlgorithmException | CertificateEncodingException var4) {
            log.error("Could not calculate kid of certificate.");
            return null;
        }
    }

    public String getCertKid(X509CertificateHolder x509CertificateHolder) {
        try {
            byte[] hashBytes = this.calculateHashBytes(x509CertificateHolder.getEncoded());
            byte[] kidBytes = Arrays.copyOfRange(hashBytes, 0, 8);
            return Base64.getEncoder().encodeToString(kidBytes);
        } catch (IOException | NoSuchAlgorithmException var4) {
            log.error("Could not calculate kid of certificate.");
            return null;
        }
    }

    public String getCertThumbprint(X509Certificate x509Certificate) {
        try {
            return this.calculateHash(x509Certificate.getEncoded());
        } catch (CertificateEncodingException | NoSuchAlgorithmException var3) {
            log.error("Could not calculate thumbprint of certificate.");
            return null;
        }
    }

    public String getCertThumbprint(X509CertificateHolder x509CertificateHolder) {
        try {
            return this.calculateHash(x509CertificateHolder.getEncoded());
        } catch (NoSuchAlgorithmException | IOException var3) {
            log.error("Could not calculate thumbprint of certificate.");
            return null;
        }
    }

    public X509CertificateHolder convertCertificate(X509Certificate inputCertificate) throws CertificateEncodingException, IOException {
        return new X509CertificateHolder(inputCertificate.getEncoded());
    }

    public X509Certificate convertCertificate(X509CertificateHolder inputCertificate) throws CertificateException {
        try {
            return (X509Certificate)this.certificateFactory.engineGenerateCertificate(new ByteArrayInputStream(inputCertificate.getEncoded()));
        } catch (IOException var3) {
            throw new CertificateException(var3.getMessage(), var3.getCause());
        }
    }

    public String calculateHash(byte[] data) throws NoSuchAlgorithmException {
        byte[] certHashBytes = MessageDigest.getInstance("SHA-256").digest(data);
        return Hex.toHexString(certHashBytes);
    }

    private byte[] calculateHashBytes(byte[] data) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256").digest(data);
    }
}

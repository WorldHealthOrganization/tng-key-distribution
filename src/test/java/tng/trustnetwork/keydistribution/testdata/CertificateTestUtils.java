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

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class CertificateTestUtils {

    public static X509Certificate generateCertificate(KeyPair keyPair, String country, String commonName,
                                                      SignerType signerType) throws Exception {
        Date validFrom = Date.from(Instant.now().minus(1, ChronoUnit.DAYS));
        Date validTo = Date.from(Instant.now().plus(365, ChronoUnit.DAYS));

        return generateCertificate(keyPair, country, commonName, validFrom, validTo, signerType);
    }

    public static X509Certificate generateCertificate(KeyPair keyPair, String country, String commonName,
                                                      X509Certificate ca, PrivateKey caKey,
                                                      SignerType signerType) throws Exception {
        Date validFrom = Date.from(Instant.now().minus(1, ChronoUnit.DAYS));
        Date validTo = Date.from(Instant.now().plus(365, ChronoUnit.DAYS));

        return generateCertificate(keyPair, country, commonName, validFrom, validTo, ca, caKey, signerType);
    }

    public static X509Certificate generateCertificate(KeyPair keyPair, String country, String commonName,
                                                      Date validFrom, Date validTo,
                                                      SignerType signerType) throws Exception {
        X500Name subject = new X500NameBuilder()
            .addRDN(X509ObjectIdentifiers.countryName, country)
            .addRDN(X509ObjectIdentifiers.commonName, commonName)
            .build();

        BigInteger certSerial = new BigInteger(Long.toString(System.currentTimeMillis()));

        ContentSigner contentSigner = new JcaContentSignerBuilder(signerType.signingMethod).build(keyPair.getPrivate());

        JcaX509v3CertificateBuilder certBuilder =
            new JcaX509v3CertificateBuilder(subject, certSerial, validFrom, validTo, subject, keyPair.getPublic());

        BasicConstraints basicConstraints = new BasicConstraints(false);
        certBuilder.addExtension(Extension.basicConstraints, true, basicConstraints);

        return new JcaX509CertificateConverter().getCertificate(certBuilder.build(contentSigner));
    }

    public static X509Certificate generateCertificate(KeyPair keyPair, String country, String commonName,
                                                      Date validFrom, Date validTo, X509Certificate ca,
                                                      PrivateKey caKey, SignerType signerType) throws Exception {
        X500Name subject = new X500NameBuilder()
            .addRDN(X509ObjectIdentifiers.countryName, country)
            .addRDN(X509ObjectIdentifiers.commonName, commonName)
            .build();

        X500Name issuer = new X509CertificateHolder(ca.getEncoded()).getSubject();

        BigInteger certSerial = new BigInteger(Long.toString(System.currentTimeMillis()));

        ContentSigner contentSigner = new JcaContentSignerBuilder(signerType.signingMethod).build(caKey);

        JcaX509v3CertificateBuilder certBuilder =
            new JcaX509v3CertificateBuilder(issuer, certSerial, validFrom, validTo, subject, keyPair.getPublic());

        BasicConstraints basicConstraints = new BasicConstraints(false);
        certBuilder.addExtension(Extension.basicConstraints, true, basicConstraints);

        return new JcaX509CertificateConverter().getCertificate(certBuilder.build(contentSigner));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class SignerType {

        private final String signingMethod;
        private final String signingAlgorithm;
        private final AlgorithmParameterSpec signingAlgorithmSpec;

        public static SignerType RSA = new SignerType("SHA256withRSA", "RSA", new RSAKeyGenParameterSpec(2048, BigInteger.valueOf(65537L)));
        public static SignerType EC = new SignerType("SHA256withECDSA", "EC", new ECGenParameterSpec("secp256r1"));
    }
}

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

package tng.trustnetwork.keydistribution.service;

import static org.mockito.Mockito.doNothing;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.dgc.gateway.connector.DgcGatewayDownloadConnector;
import eu.europa.ec.dgc.utils.CertificateUtils;
import foundation.identity.jsonld.JsonLDObject;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tng.trustnetwork.keydistribution.entity.SignerInformationEntity;
import tng.trustnetwork.keydistribution.entity.TrustedPartyEntity;
import tng.trustnetwork.keydistribution.repository.SignerInformationRepository;
import tng.trustnetwork.keydistribution.repository.TrustedIssuerRepository;
import tng.trustnetwork.keydistribution.repository.TrustedPartyRepository;
import tng.trustnetwork.keydistribution.service.did.DidTrustListService;
import tng.trustnetwork.keydistribution.service.did.DidUploader;
import tng.trustnetwork.keydistribution.service.did.entity.DidTrustList;
import tng.trustnetwork.keydistribution.testdata.CertificateTestUtils;
import tng.trustnetwork.keydistribution.testdata.TrustedIssuerTestHelper;

@SpringBootTest
public class DidTrustListServiceTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    DidTrustListService didTrustListService;

    @Autowired
    SignerInformationRepository signerInformationRepository;

    @Autowired
    TrustedPartyRepository trustedPartyRepository;

    @Autowired
    CertificateUtils certificateUtils;

    @Autowired
    TrustedIssuerRepository trustedIssuerRepository;

    @Autowired
    TrustedIssuerTestHelper trustedIssuerTestHelper;

    @MockBean
    DidUploader didUploaderMock;

    @MockBean
    DgcGatewayDownloadConnector dgcGatewayDownloadConnector;

    X509Certificate certCscaDe, certCscaEu, certDscDe, certDscEu;

    String certDscDeKid, certDscEuKid;


    @AfterEach
    public void cleanUp() {

        trustedPartyRepository.deleteAll();
        signerInformationRepository.deleteAll();
        trustedIssuerRepository.deleteAll();
    }

    void testData(CertificateTestUtils.SignerType signerType) throws Exception {
        cleanUp();

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(signerType.getSigningAlgorithm());

        KeyPair cscaDeKeyPair = keyPairGenerator.generateKeyPair();
        certCscaDe = CertificateTestUtils.generateCertificate(cscaDeKeyPair, "DE", "Test", signerType);
        KeyPair cscaEuKeyPair = keyPairGenerator.generateKeyPair();
        certCscaEu = CertificateTestUtils.generateCertificate(cscaEuKeyPair, "EU", "Test", signerType);

        certDscDe = CertificateTestUtils.generateCertificate(keyPairGenerator.generateKeyPair(), "DE",
                    "Test", certCscaDe, cscaDeKeyPair.getPrivate(), signerType);
        certDscDeKid = certificateUtils.getCertKid(certDscDe);

        certDscEu = CertificateTestUtils.generateCertificate(keyPairGenerator.generateKeyPair(), "EU",
                    "Test", certCscaEu, cscaEuKeyPair.getPrivate(), signerType);
        certDscEuKid = certificateUtils.getCertKid(certDscEu);

        trustedPartyRepository.save(new TrustedPartyEntity(
            null,
            Base64.getEncoder().encodeToString(certCscaDe.getEncoded()),
            "DE",
            TrustedPartyEntity.Type.CSCA
        ));

        trustedPartyRepository.save(new TrustedPartyEntity(
            null,
            Base64.getEncoder().encodeToString(certCscaEu.getEncoded()),
            "EU",
            TrustedPartyEntity.Type.CSCA
        ));

        signerInformationRepository.save(new SignerInformationEntity(
            null,
            certDscDeKid,
            ZonedDateTime.now(),
            Base64.getEncoder().encodeToString(certDscDe.getEncoded()),
            "DE",
            certificateUtils.getCertThumbprint(certDscDe),
            ZonedDateTime.now(),
            false
        ));

        signerInformationRepository.save(new SignerInformationEntity(
            null,
            certDscEuKid,
            ZonedDateTime.now(),
            Base64.getEncoder().encodeToString(certDscEu.getEncoded()),
            "EU",
            certificateUtils.getCertThumbprint(certDscEu),
            ZonedDateTime.now(),
            false
        ));

        trustedIssuerRepository.save(trustedIssuerTestHelper.createTrustedIssuer("DE"));
        trustedIssuerRepository.save(trustedIssuerTestHelper.createTrustedIssuer("EU"));
        trustedIssuerRepository.save(trustedIssuerTestHelper.createTrustedIssuer("XY"));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testTrustList(boolean isEcAlgorithm) throws Exception {
        if (isEcAlgorithm) {
            testData(CertificateTestUtils.SignerType.EC);
        } else {
            testData(CertificateTestUtils.SignerType.RSA);
        }
        ArgumentCaptor<byte[]> uploadArgumentCaptor = ArgumentCaptor.forClass(byte[].class);
        doNothing().when(didUploaderMock).uploadDid(uploadArgumentCaptor.capture());

        didTrustListService.job();

        SignedDidTrustList parsed =
            objectMapper.readValue(uploadArgumentCaptor.getValue(), SignedDidTrustList.class);

        Assertions.assertEquals("did:web:abc", parsed.getId());
        Assertions.assertEquals("did:web:def", parsed.getController());
        Assertions.assertEquals(5, parsed.getVerificationMethod().size());

        assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(), "did:web:abc:deu#" + URLEncoder.encode(certDscDeKid, StandardCharsets.UTF_8)),
            certDscDeKid, certDscDe, certCscaDe, "deu");
        assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(), "did:web:abc:xeu#" + URLEncoder.encode(certDscEuKid, StandardCharsets.UTF_8)),
            certDscEuKid, certDscEu, certCscaEu, "xeu");

        Assertions.assertTrue(parsed.getVerificationMethod().contains("did:trusted:DE:issuer"));
        Assertions.assertTrue(parsed.getVerificationMethod().contains("did:trusted:EU:issuer"));
        Assertions.assertTrue(parsed.getVerificationMethod().contains("did:trusted:XY:issuer"));
        Assertions.assertEquals(2, parsed.getContext().size());
        Assertions.assertEquals("JsonWebSignature2020", parsed.getProof().getType());
        Assertions.assertTrue(
            Instant.now().toEpochMilli() - parsed.getProof().getCreated().toInstant().toEpochMilli() < 10000);
        Assertions.assertEquals("d0m4in", parsed.getProof().getDomain());
        Assertions.assertEquals("n0nc3", parsed.getProof().getNonce());
        Assertions.assertEquals("assertionMethod", parsed.getProof().getProofPurpose());
        Assertions.assertEquals("did:web:dummy.net", parsed.getProof().getVerificationMethod());
        Assertions.assertNotNull(parsed.getProof().getJws());
        Assertions.assertNotEquals("", parsed.getProof().getJws());

        //JSON should start with "@context" due to https://www.w3.org/TR/json-ld11-streaming/#key-ordering-required
        String json = JsonLDObject.fromJson(objectMapper.writeValueAsString(parsed)).toJson();
        String first10Characters = json.substring(0, Math.min(10, json.length()));
        Assertions.assertTrue(first10Characters.contains("@context"));
    }


    private Object getVerificationMethodByKid(List<Object> verificationMethods, String kid) {
        return verificationMethods.stream()
            .filter(entry -> entry instanceof LinkedHashMap<?,?>)
            .map(entry -> (LinkedHashMap<?, ?>) entry)
            .filter(entry -> entry.get("id").equals(kid))
            .findFirst()
            .orElseGet(() -> Assertions.fail("Could not find VerificationMethod with KID " + kid));
    }

    private void assertVerificationMethod(Object in, String kid, X509Certificate dsc, X509Certificate csca, String country)
            throws CertificateEncodingException {
        LinkedHashMap<?, ?> jsonNode = (LinkedHashMap<?, ?>) in;
        Assertions.assertEquals("JsonWebKey2020", jsonNode.get("type"));
        Assertions.assertEquals("did:web:abc:" + country, jsonNode.get("controller"));
        Assertions.assertEquals("did:web:abc:" + country + "#" + URLEncoder.encode(kid, StandardCharsets.UTF_8), jsonNode.get("id"));

        LinkedHashMap<?, ?> publicKeyJwk = (LinkedHashMap<?, ?>) jsonNode.get("publicKeyJwk");

        if (dsc.getPublicKey().getAlgorithm().equals(CertificateTestUtils.SignerType.EC.getSigningAlgorithm())) {
            Assertions.assertEquals(((ECPublicKey) dsc.getPublicKey()).getW().getAffineX(),
                    new BigInteger(Base64.getDecoder().decode(publicKeyJwk.get("x").toString())));
            Assertions.assertEquals(((ECPublicKey) dsc.getPublicKey()).getW().getAffineY(),
                    new BigInteger(Base64.getDecoder().decode(publicKeyJwk.get("y").toString())));
            Assertions.assertEquals(CertificateTestUtils.SignerType.EC.getSigningAlgorithm(),
                    publicKeyJwk.get("kty").toString());
            Assertions.assertEquals("P-256", publicKeyJwk.get("crv").toString());
        } else {
            Assertions.assertEquals(((RSAPublicKey) dsc.getPublicKey()).getPublicExponent(),
                    new BigInteger(Base64.getDecoder().decode(publicKeyJwk.get("e").toString())));
            Assertions.assertEquals(((RSAPublicKey) dsc.getPublicKey()).getModulus(),
                    new BigInteger(Base64.getDecoder().decode(publicKeyJwk.get("n").toString())));
            Assertions.assertEquals(CertificateTestUtils.SignerType.RSA.getSigningAlgorithm(),
                    publicKeyJwk.get("kty").toString());
        }
        ArrayList<String> x5c = ((ArrayList<String>) publicKeyJwk.get("x5c"));
        Assertions.assertEquals(Base64.getEncoder().encodeToString(dsc.getEncoded()), x5c.get(0));
        if (csca != null) {
            Assertions.assertEquals(Base64.getEncoder().encodeToString(csca.getEncoded()), x5c.get(1));
        }
    }

    @Getter
    @Setter
    public static class SignedDidTrustList extends DidTrustList {

        private LDProof proof;

        @Data
        private static class LDProof {

            private String type;

            private ZonedDateTime created;

            private String verificationMethod;

            private String proofPurpose;

            private String jws;

            private String domain;

            private String nonce;

        }
    }
}

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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.Base64URL;
import eu.europa.ec.dgc.gateway.connector.DgcGatewayDownloadConnector;
import eu.europa.ec.dgc.utils.CertificateUtils;
import foundation.identity.jsonld.JsonLDObject;
import java.math.BigInteger;
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
import tng.trustnetwork.keydistribution.repository.SignerInformationRepository;
import tng.trustnetwork.keydistribution.repository.TrustedIssuerRepository;
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
    CertificateUtils certificateUtils;

    @Autowired
    TrustedIssuerRepository trustedIssuerRepository;

    @Autowired
    TrustedIssuerTestHelper trustedIssuerTestHelper;

    @MockBean
    DidUploader didUploaderMock;

    @MockBean
    DgcGatewayDownloadConnector dgcGatewayDownloadConnector;

    X509Certificate certCscaDe, certCscaEu, certDscDe, certDscEu, certUploadDe;

    String certDscDeKid, certDscEuKid, certCscaDeKid, certCscaEuKid, certUploadDeKid;

    private static final String SEPARATOR_DID_PATH = ":";
    @AfterEach
    public void cleanUp() {

        signerInformationRepository.deleteAll();
        trustedIssuerRepository.deleteAll();
    }

    void testData(CertificateTestUtils.SignerType signerType) throws Exception {

        cleanUp();

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(signerType.getSigningAlgorithm());
        keyPairGenerator.initialize(signerType.getSigningAlgorithmSpec());

        KeyPair cscaDeKeyPair = keyPairGenerator.generateKeyPair();
        certCscaDe = CertificateTestUtils.generateCertificate(cscaDeKeyPair, "DE", "Test", signerType);
        certCscaDeKid = certificateUtils.getCertKid(certCscaDe);

        KeyPair cscaEuKeyPair = keyPairGenerator.generateKeyPair();
        certCscaEu = CertificateTestUtils.generateCertificate(cscaEuKeyPair, "EU", "Test", signerType);
        certCscaEuKid = certificateUtils.getCertKid(certCscaEu);

        certDscDe = CertificateTestUtils.generateCertificate(keyPairGenerator.generateKeyPair(), "DE",
                                                             "Test", certCscaDe, cscaDeKeyPair.getPrivate(),
                                                             signerType);
        certDscDeKid = certificateUtils.getCertKid(certDscDe);

        certDscEu = CertificateTestUtils.generateCertificate(keyPairGenerator.generateKeyPair(), "EU",
                                                             "Test", certCscaEu, cscaEuKeyPair.getPrivate(),
                                                             signerType);
        certDscEuKid = certificateUtils.getCertKid(certDscEu);

        certUploadDe = CertificateTestUtils.generateCertificate(keyPairGenerator.generateKeyPair(), "DE",
                                                                "Upload Test", certCscaDe, cscaDeKeyPair.getPrivate(),
                                                                signerType);
        certUploadDeKid = certificateUtils.getCertKid(certUploadDe);

        signerInformationRepository.save(new SignerInformationEntity(
            null,
            certCscaDeKid,
            ZonedDateTime.now(),
            Base64.getEncoder().encodeToString(certCscaDe.getEncoded()),
            "DE",
            "DCC",
            "CSCA",
            certificateUtils.calculateHash(certCscaDe.getSubjectX500Principal().getEncoded())
        ));

        signerInformationRepository.save(new SignerInformationEntity(
            null,
            certCscaEuKid,
            ZonedDateTime.now(),
            Base64.getEncoder().encodeToString(certCscaEu.getEncoded()),
            "EU",
            "DCC",
            "CSCA",
            certificateUtils.calculateHash(certCscaEu.getSubjectX500Principal().getEncoded())
        ));

        signerInformationRepository.save(new SignerInformationEntity(
            null,
            certDscDeKid,
            ZonedDateTime.now(),
            Base64.getEncoder().encodeToString(certDscDe.getEncoded()),
            "DE",
            "DCC",
            "DSC",
            certificateUtils.calculateHash(certDscDe.getSubjectX500Principal().getEncoded())
        ));

        signerInformationRepository.save(new SignerInformationEntity(
            null,
            certDscEuKid,
            ZonedDateTime.now(),
            Base64.getEncoder().encodeToString(certDscEu.getEncoded()),
            "EU",
            "DCC",
            "DSC",
            certificateUtils.calculateHash(certDscEu.getSubjectX500Principal().getEncoded())
        ));

        // Add Upload cert which should not be added to did
        signerInformationRepository.save(new SignerInformationEntity(
            null,
            certUploadDeKid,
            ZonedDateTime.now(),
            Base64.getEncoder().encodeToString(certUploadDe.getEncoded()),
            "DE",
            "DCC",
            "UPLOAD",
            certificateUtils.calculateHash(certUploadDe.getSubjectX500Principal().getEncoded())
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
        doNothing().when(didUploaderMock).uploadDid(anyString(), uploadArgumentCaptor.capture());

        didTrustListService.job();

        Assertions.assertEquals(70, uploadArgumentCaptor.getAllValues().size());

        int expectedNullDid = 32;

        for (byte[] uploadedDid : uploadArgumentCaptor.getAllValues()) {

            if (uploadedDid == null) {
                expectedNullDid--;

                Assertions.assertTrue(expectedNullDid >= 0, "DID Collection contains more empty documents than expected. (" + expectedNullDid * -1 + " too much)");
                continue;
            }

            SignedDidTrustList parsed = objectMapper.readValue(uploadedDid, SignedDidTrustList.class);

            checkJsonDocument(parsed);

            if(parsed.getId().split(SEPARATOR_DID_PATH).length >= 8) continue;

            switch (parsed.getId()) {
                case "did:web:abc:trustlist":
                    Assertions.assertEquals("did:web:abc:trustlist", parsed.getController());
                    Assertions.assertEquals(4, parsed.getVerificationMethod().size());

                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist" , certDscDeKid),
                                             certDscDeKid, certDscDe, certCscaDe, "did:web:abc:trustlist");
                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist" , certCscaDeKid),
                                             certCscaDeKid, certCscaDe, null, "did:web:abc:trustlist");
                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist",certDscEuKid),
                                             certDscEuKid, certDscEu, certCscaEu, "did:web:abc:trustlist");
                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist",certCscaEuKid),
                                             certCscaEuKid, certCscaEu, null, "did:web:abc:trustlist");
                    break;
                case "did:web:abc:trustlist:-":
                    Assertions.assertEquals("did:web:abc:trustlist", parsed.getController());
                    Assertions.assertEquals(4, parsed.getVerificationMethod().size());

                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:-",certDscDeKid),
                                             certDscDeKid, certDscDe, certCscaDe, "did:web:abc:trustlist:-");
                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:-",certCscaDeKid),
                                             certCscaDeKid, certCscaDe, null, "did:web:abc:trustlist:-");
                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:-",certDscEuKid),
                                             certDscEuKid, certDscEu, certCscaEu, "did:web:abc:trustlist:-");
                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:-",certCscaEuKid),
                                             certCscaEuKid, certCscaEu, null, "did:web:abc:trustlist:-");
                    break;

                case "did:web:abc:trustlist:-:-":
                    Assertions.assertEquals("did:web:abc:trustlist:-", parsed.getController());
                    Assertions.assertEquals(4, parsed.getVerificationMethod().size());

                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:-:-",certDscDeKid),
                                             certDscDeKid, certDscDe, certCscaDe, "did:web:abc:trustlist:-:-");
                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:-:-",certDscEuKid),
                                             certDscEuKid, certDscEu, certCscaEu, "did:web:abc:trustlist:-:-");
                    break;

                case "did:web:abc:trustlist:DCC:-":
                    Assertions.assertEquals("did:web:abc:trustlist:DCC", parsed.getController());
                    Assertions.assertEquals(4, parsed.getVerificationMethod().size());

                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:DCC:-",certDscDeKid),
                                             certDscDeKid, certDscDe, certCscaDe, "did:web:abc:trustlist:DCC:-");
                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:DCC:-",certDscEuKid),
                                             certDscEuKid, certDscEu, certCscaEu, "did:web:abc:trustlist:DCC:-");
                    break;


                case "did:web:abc:trustlist:DCC:XEU:DSC":
                    Assertions.assertEquals("did:web:abc:trustlist:DCC:XEU", parsed.getController());
                    Assertions.assertEquals(1, parsed.getVerificationMethod().size());

                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:DCC:XEU:DSC",certDscEuKid),
                                             certDscEuKid, certDscEu, certCscaEu, "did:web:abc:trustlist:DCC:XEU:DSC");
                    break;

                case "did:web:abc:trustlist:DCC":
                    Assertions.assertEquals("did:web:abc:trustlist", parsed.getController());
                    Assertions.assertEquals(4, parsed.getVerificationMethod().size());

                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:DCC", certDscDeKid),
                                             certDscDeKid, certDscDe, certCscaDe, "did:web:abc:trustlist:DCC");
                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:DCC", certDscEuKid),
                                             certDscEuKid, certDscEu, certCscaEu, "did:web:abc:trustlist:DCC");
                    break;

                case "did:web:abc:trustlist:-:XEU":
                    Assertions.assertEquals("did:web:abc:trustlist:-", parsed.getController());
                    Assertions.assertEquals(2, parsed.getVerificationMethod().size());

                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:-:XEU",certCscaEuKid),
                                             certCscaEuKid, certCscaEu, null, "did:web:abc:trustlist:-:XEU");
                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:-:XEU",certDscEuKid),
                                             certDscEuKid, certDscEu, certCscaEu, "did:web:abc:trustlist:-:XEU");
                    break;

                case "did:web:abc:trustlist:-:DEU":
                    Assertions.assertEquals("did:web:abc:trustlist:-", parsed.getController());
                    Assertions.assertEquals(2, parsed.getVerificationMethod().size());

                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:-:DEU",certDscDeKid),
                                             certDscDeKid, certDscDe, certCscaDe, "did:web:abc:trustlist:-:DEU");
                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:-:DEU",certCscaDeKid),
                                             certCscaDeKid, certCscaDe, null, "did:web:abc:trustlist:-:DEU");
                    break;

                case "did:web:abc:trustlist:DCC:XEU:CSA":
                    Assertions.assertEquals("did:web:abc:trustlist:DCC:XEU", parsed.getController());
                    Assertions.assertEquals(1, parsed.getVerificationMethod().size());

                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:DCC:XEU:CSA",certCscaEuKid),
                                             certCscaEuKid, certCscaEu, null, "did:web:abc:trustlist:DCC:XEU:CSA");
                    break;

                case "did:web:abc:trustlist:DCC:DEU:DSC":
                    Assertions.assertEquals("did:web:abc:trustlist:DCC:DEU", parsed.getController());
                    Assertions.assertEquals(1, parsed.getVerificationMethod().size());

                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:DCC:DEU:DSC",certDscDeKid),
                                             certDscDeKid, certDscDe, certCscaDe, "did:web:abc:trustlist:DCC:DEU:DSC");
                    break;

                case "did:web:abc:trustlist:DCC:DEU:CSA":
                    Assertions.assertEquals("did:web:abc:trustlist:DCC:DEU", parsed.getController());
                    Assertions.assertEquals(1, parsed.getVerificationMethod().size());

                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:DCC:DEU:CSA",certCscaDeKid),
                                             certCscaDeKid, certCscaDe, null, "did:web:abc:trustlist:DCC:DEU:CSA");

                    break;

                case "did:web:abc:trustlist:DCC:DEU":
                    Assertions.assertEquals("did:web:abc:trustlist:DCC", parsed.getController());
                    Assertions.assertEquals(2, parsed.getVerificationMethod().size());

                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:DCC:DEU",certDscDeKid),
                                             certDscDeKid, certDscDe, certCscaDe, "did:web:abc:trustlist:DCC:DEU");
                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:DCC:DEU",certCscaDeKid),
                                             certCscaDeKid, certCscaDe, null, "did:web:abc:trustlist:DCC:DEU");
                    break;

                case "did:web:abc:trustlist:DCC:XEU":
                    Assertions.assertEquals("did:web:abc:trustlist:DCC", parsed.getController());
                    Assertions.assertEquals(2, parsed.getVerificationMethod().size());

                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:DCC:XEU",certDscEuKid),
                                             certDscEuKid, certDscEu, certCscaEu, "did:web:abc:trustlist:DCC:XEU");
                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:DCC:XEU",certCscaEuKid),
                                             certCscaEuKid, certCscaEu, null, "did:web:abc:trustlist:DCC:XEU");
                    break;

                case "did:web:abc:trustlist:-:XEU:DSC":
                    Assertions.assertEquals("did:web:abc:trustlist:-:XEU", parsed.getController());
                    Assertions.assertEquals(1, parsed.getVerificationMethod().size());

                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:-:XEU:DSC",certDscEuKid),
                                             certDscEuKid, certDscEu, certCscaEu, "did:web:abc:trustlist:-:XEU:DSC");
                    break;

                case "did:web:abc:trustlist:-:DEU:DSC":
                    Assertions.assertEquals("did:web:abc:trustlist:-:DEU", parsed.getController());
                    Assertions.assertEquals(1, parsed.getVerificationMethod().size());

                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:-:DEU:DSC",certDscDeKid),
                                             certDscDeKid, certDscDe, certCscaDe, "did:web:abc:trustlist:-:DEU:DSC");
                    break;

                case "did:web:abc:trustlist:-:DEU:CSA":
                    Assertions.assertEquals("did:web:abc:trustlist:-:DEU", parsed.getController());
                    Assertions.assertEquals(1, parsed.getVerificationMethod().size());

                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:-:DEU:CSA",certCscaDeKid),
                                             certCscaDeKid, certCscaDe, null, "did:web:abc:trustlist:-:DEU:CSA");
                    break;

                case "did:web:abc:trustlist:-:-:CSA":
                    Assertions.assertEquals("did:web:abc:trustlist:-:-", parsed.getController());
                    Assertions.assertEquals(2, parsed.getVerificationMethod().size());

                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:-:-:CSA",certCscaEuKid),
                                             certCscaEuKid, certCscaEu, null, "did:web:abc:trustlist:-:-:CSA");
                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:-:-:CSA",certCscaDeKid),
                                             certCscaDeKid, certCscaDe, null, "did:web:abc:trustlist:-:-:CSA");
                    break;

                case "did:web:abc:trustlist:-:-:DSC":
                    Assertions.assertEquals("did:web:abc:trustlist:-:-", parsed.getController());
                    Assertions.assertEquals(2, parsed.getVerificationMethod().size());

                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:-:-:DSC",certDscEuKid),
                                             certDscEuKid, certDscEu, certCscaEu, "did:web:abc:trustlist:-:-:DSC");
                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:-:-:DSC",certDscDeKid),
                                             certDscDeKid, certDscDe, certCscaDe, "did:web:abc:trustlist:-:-:DSC");
                    break;

                case "did:web:abc:trustlist:-:XEU:CSA":
                    Assertions.assertEquals("did:web:abc:trustlist:-:XEU", parsed.getController());
                    Assertions.assertEquals(1, parsed.getVerificationMethod().size());

                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:-:XEU:CSA",certCscaEuKid),
                                             certCscaEuKid, certCscaEu, null, "did:web:abc:trustlist:-:XEU:CSA");
                    break;

                case "did:web:abc:trustlist:DCC:-:DSC":
                    Assertions.assertEquals("did:web:abc:trustlist:DCC:-", parsed.getController());
                    Assertions.assertEquals(2, parsed.getVerificationMethod().size());

                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:DCC:-:DSC",certDscDeKid),
                                             certDscDeKid, certDscDe, certCscaDe, "did:web:abc:trustlist:DCC:-:DSC");
                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:DCC:-:DSC",certDscEuKid),
                                             certDscEuKid, certDscEu, certCscaEu, "did:web:abc:trustlist:DCC:-:DSC");
                    break;

                case "did:web:abc:trustlist:DCC:-:CSA":
                    Assertions.assertEquals("did:web:abc:trustlist:DCC:-", parsed.getController());
                    Assertions.assertEquals(2, parsed.getVerificationMethod().size());

                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:DCC:-:CSA",certCscaDeKid),
                                             certCscaDeKid, certCscaDe, null, "did:web:abc:trustlist:DCC:-:CSA");
                    assertVerificationMethod(getVerificationMethodByKid(parsed.getVerificationMethod(),"did:web:abc:trustlist:DCC:-:CSA",certCscaEuKid),
                                             certCscaEuKid, certCscaEu, null, "did:web:abc:trustlist:DCC:-:CSA");
                    break;

                case "did:web:abc:trustlist:-:XY":
                    Assertions.assertEquals("did:web:abc:trustlist:-", parsed.getController());
                    Assertions.assertEquals(0, parsed.getVerificationMethod().size());
                    break;

                case "did:web:abc:trustlist:DCC:XY":
                    Assertions.assertEquals("did:web:abc:trustlist:DCC", parsed.getController());
                    Assertions.assertEquals(0, parsed.getVerificationMethod().size());
                    break;

                case "did:web:abc:trustlist-ref:DCC:EU":
                    Assertions.assertFalse(parsed.getVerificationMethod().contains("did:trusted:DE:issuer"));
                    Assertions.assertTrue(parsed.getVerificationMethod().contains("did:trusted:EU:issuer"));
                    Assertions.assertFalse(parsed.getVerificationMethod().contains("did:trusted:XY:issuer"));
                    break;

                case "did:web:abc:trustlist-ref:DCC:XY":
                    Assertions.assertFalse(parsed.getVerificationMethod().contains("did:trusted:DE:issuer"));
                    Assertions.assertFalse(parsed.getVerificationMethod().contains("did:trusted:EU:issuer"));
                    Assertions.assertTrue(parsed.getVerificationMethod().contains("did:trusted:XY:issuer"));
                    break;

                case "did:web:abc:trustlist-ref:DCC:DE":
                    Assertions.assertTrue(parsed.getVerificationMethod().contains("did:trusted:DE:issuer"));
                    Assertions.assertFalse(parsed.getVerificationMethod().contains("did:trusted:EU:issuer"));
                    Assertions.assertFalse(parsed.getVerificationMethod().contains("did:trusted:XY:issuer"));
                    break;

                default:
                    if (!parsed.getId().contains("trustlist-ref")) {
                        Assertions.fail("Unexpected Document in DID Collection! (" +  parsed.getId() + ")");
                    }
            }
        }
    }

    private void checkJsonDocument(SignedDidTrustList parsed) throws JsonProcessingException {

        Assertions.assertEquals(3, parsed.getContext().size());
        Assertions.assertEquals("JsonWebSignature2020", parsed.getProof().getType());
        Assertions.assertTrue(
            Instant.now().toEpochMilli() - parsed.getProof().getCreated().toInstant().toEpochMilli() < 10000);
        Assertions.assertEquals("d0m4in", parsed.getProof().getDomain());
        Assertions.assertEquals(32, parsed.getProof().getNonce().length());
        Assertions.assertEquals("assertionMethod", parsed.getProof().getProofPurpose());
        Assertions.assertEquals("did:web:dummy.net", parsed.getProof().getVerificationMethod());
        Assertions.assertNotNull(parsed.getProof().getJws());
        Assertions.assertNotEquals("", parsed.getProof().getJws());

        //JSON should start with "@context" due to https://www.w3.org/TR/json-ld11-streaming/#key-ordering-required
        String json = JsonLDObject.fromJson(objectMapper.writeValueAsString(parsed)).toJson();
        String first10Characters = json.substring(0, Math.min(10, json.length()));
        Assertions.assertTrue(first10Characters.contains("@context"));
    }


    private Object getVerificationMethodByKid(List<Object> verificationMethods, String parentId,  String kid) {

        return verificationMethods.stream()
                                  .filter(entry -> entry instanceof LinkedHashMap<?, ?>)
                                  .map(entry -> (LinkedHashMap<?, ?>) entry)
                                  .filter(entry -> entry.get("id").toString().contains(parentId) && entry.get("id").toString().contains(kid))
                                  .findFirst()
                                  .orElseGet(
                                      () -> Assertions.fail("Could not find VerificationMethod with KID " + kid));
    }

    private void assertVerificationMethod(Object in, String kid, X509Certificate dsc, X509Certificate csca,
                                          String parentDidId)
        throws CertificateEncodingException {

        LinkedHashMap<?, ?> jsonNode = (LinkedHashMap<?, ?>) in;
        Assertions.assertEquals("JsonWebKey2020", jsonNode.get("type"));
        Assertions.assertTrue(jsonNode.get("id").toString().contains(parentDidId) && jsonNode.get("id").toString().contains(kid));


        LinkedHashMap<?, ?> publicKeyJwk = (LinkedHashMap<?, ?>) jsonNode.get("publicKeyJwk");

        if (dsc.getPublicKey().getAlgorithm().equals(CertificateTestUtils.SignerType.EC.getSigningAlgorithm())) {
            Assertions.assertEquals(((ECPublicKey) dsc.getPublicKey()).getW().getAffineX(),
                                    new BigInteger(Base64.getUrlDecoder().decode(publicKeyJwk.get("x").toString())));
            Assertions.assertEquals(((ECPublicKey) dsc.getPublicKey()).getW().getAffineY(),
                                    new BigInteger(Base64.getUrlDecoder().decode(publicKeyJwk.get("y").toString())));
            Assertions.assertEquals(CertificateTestUtils.SignerType.EC.getSigningAlgorithm(),
                                    publicKeyJwk.get("kty").toString());
            Assertions.assertEquals("P-256", publicKeyJwk.get("crv").toString());
            Assertions.assertEquals(kid, publicKeyJwk.get("kid"));
        } else {
            Assertions.assertEquals(((RSAPublicKey) dsc.getPublicKey()).getPublicExponent(),
                                    new BigInteger(Base64.getUrlDecoder().decode(publicKeyJwk.get("e").toString())));
            Assertions.assertEquals(((RSAPublicKey) dsc.getPublicKey()).getModulus(),
                                    new BigInteger(Base64.getUrlDecoder().decode(publicKeyJwk.get("n").toString())));
            Assertions.assertEquals(CertificateTestUtils.SignerType.RSA.getSigningAlgorithm(),
                                    publicKeyJwk.get("kty").toString());
            Assertions.assertEquals(kid, publicKeyJwk.get("kid"));
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

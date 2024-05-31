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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.dgc.gateway.connector.DgcGatewayDownloadConnector;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tng.trustnetwork.keydistribution.entity.DecentralizedIdentifierEntity;
import tng.trustnetwork.keydistribution.entity.EcPublicKeyJwkEntity;
import tng.trustnetwork.keydistribution.model.DidDocument;
import tng.trustnetwork.keydistribution.repository.DecentralizedIdentifierRepository;

@SpringBootTest
public class DecentralizedIdentifierServiceTest {

    @MockBean
    DgcGatewayDownloadConnector dgcGatewayDownloadConnector;

    @Autowired
    DecentralizedIdentifierRepository decentralizedIdentifierRepository;

    @Autowired
    DecentralizedIdentifierService decentralizedIdentifierService;

    @Autowired
    ObjectMapper objectMapper;

    private static final String testDidId = "did:web:did.actor:mike";

    private static final String testDid = """                                  
                                          {
                                            "@context": [
                                              "https://www.w3.org/ns/did/v1",
                                              {
                                                "@base": "did:web:did.actor:mike",
                                                "rating": "https://schema.org/Rating",
                                                "publicAccess": "https://schema.org/publicAccess",
                                                "additionalType": "https://schema.org/additionalType"
                                              }
                                            ],
                                            "id": "did:web:did.actor:mike",
                                            "rating": 4.5,
                                            "publicAccess": true,
                                            "additionalType": null,
                                            "verificationMethod": [
                                              {
                                                "id": "#g1",
                                                "controller": "did:web:did.actor:mike",
                                                "type": "JsonWebKey2020",
                                                "publicKeyJwk": {
                                                  "kty": "EC",
                                                  "crv": "P-256",
                                                  "x": "xValue"
                                                }
                                              }
                                            ],
                                            "authentication": [
                                              "did:web:did.actor:mike#g1"
                                            ],
                                            "assertionMethod": [
                                              "did:web:did.actor:mike#g1"
                                            ]
                                          }
                                          """;

    @Test
    void itShouldPersistDIDsInDb() throws JsonProcessingException {

        DidDocument didDocument = objectMapper.readValue(testDid, DidDocument.class);

        decentralizedIdentifierService.updateDecentralizedIdentifierList(didDocument, testDid);

        List<DecentralizedIdentifierEntity> allDids = decentralizedIdentifierRepository.findAll();

        Assertions.assertEquals(1, allDids.size());

        DecentralizedIdentifierEntity storedDid = allDids.get(0);

        Assertions.assertEquals(testDidId, storedDid.getDidId());
        Assertions.assertEquals(testDid, storedDid.getRaw());
        Assertions.assertNotNull(storedDid.getCreatedAt());
        Assertions.assertEquals(1, storedDid.getVerificationMethods().size());
        Assertions.assertEquals("#g1", storedDid.getVerificationMethods().get(0).getVmId());
        Assertions.assertEquals("JsonWebKey2020", storedDid.getVerificationMethods().get(0).getType());
        Assertions.assertEquals(testDidId, storedDid.getVerificationMethods().get(0).getController());

        Assertions.assertInstanceOf(EcPublicKeyJwkEntity.class, storedDid.getVerificationMethods().get(0).getPublicKeyJwk());
        EcPublicKeyJwkEntity ecPublicKeyJwk =
            (EcPublicKeyJwkEntity) storedDid.getVerificationMethods().get(0).getPublicKeyJwk();
        Assertions.assertEquals("P256", ecPublicKeyJwk.getCrv());
        Assertions.assertEquals("xValue", ecPublicKeyJwk.getXvalue());

        Assertions.assertEquals(storedDid, storedDid.getVerificationMethods().get(0).getParentDocument());

    }

}

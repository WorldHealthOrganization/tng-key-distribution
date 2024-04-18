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

import eu.europa.ec.dgc.gateway.connector.DgcGatewayDownloadConnector;
import eu.europa.ec.dgc.gateway.connector.DgcGatewayTrustedIssuerDownloadConnector;
import eu.europa.ec.dgc.gateway.connector.model.TrustedIssuer;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import tng.trustnetwork.keydistribution.entity.TrustedIssuerEntity;
import tng.trustnetwork.keydistribution.repository.TrustedIssuerRepository;
import tng.trustnetwork.keydistribution.testdata.TrustedIssuerTestHelper;

@SpringBootTest
@TestPropertySource(properties = {"dgc.trustedIssuerDownloader.enabled=true"})
class TrustedIssuerDownloadServiceTest {

    @MockBean
    DgcGatewayDownloadConnector dgcGatewayDownloadConnectorMock;

    @MockBean
    DgcGatewayTrustedIssuerDownloadConnector dgcGatewayDownloadConnector;


    @Autowired
    TrustedIssuerDownloadService trustedIssuerDownloadService;

    @Autowired
    TrustedIssuerRepository trustedIssuerRepository;

    @Autowired
    TrustedIssuerTestHelper trustedIssuerTestHelper;

    @Test
    void downloadEmptyIssuerList() {
        ArrayList<TrustedIssuer> trustList = new ArrayList<>();
        Mockito.when(dgcGatewayDownloadConnector.getTrustedIssuers()).thenReturn(trustList);

        trustedIssuerDownloadService.downloadTrustedIssuers();

        List<TrustedIssuerEntity> repositoryItems = trustedIssuerRepository.findAll();
        Assertions.assertEquals(0, repositoryItems.size());
    }

    @Test
    void downloadIssuers() {
        List<TrustedIssuer> trustedIssuers = trustedIssuerTestHelper.getTrustedIssuerList();

        Mockito.when(dgcGatewayDownloadConnector.getTrustedIssuers()).thenReturn(trustedIssuers);

        trustedIssuerDownloadService.downloadTrustedIssuers();

        List<TrustedIssuerEntity> repositoryItems = trustedIssuerRepository.findAll();
        Assertions.assertEquals(1, repositoryItems.size());

        TrustedIssuer trustedIssuer = trustedIssuers.get(0);

        TrustedIssuerEntity repositoryItem = repositoryItems.get(0);
        Assertions.assertEquals(trustedIssuer.getCountry(), repositoryItem.getCountry());
        Assertions.assertEquals(trustedIssuer.getKeyStorageType(), repositoryItem.getKeyStorageType());
        Assertions.assertEquals(trustedIssuer.getName(), repositoryItem.getName());
        Assertions.assertEquals(trustedIssuer.getSignature(), repositoryItem.getSignature());
        Assertions.assertEquals(trustedIssuer.getThumbprint(), repositoryItem.getThumbprint());
        Assertions.assertEquals(trustedIssuer.getSslPublicKey(), repositoryItem.getSslPublicKey());
        Assertions.assertEquals(trustedIssuer.getUrl(), repositoryItem.getUrl());
        Assertions.assertEquals(trustedIssuer.getType().toString(), repositoryItem.getUrlType().toString());
    }

}

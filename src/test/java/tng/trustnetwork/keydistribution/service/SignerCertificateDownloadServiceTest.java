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
import eu.europa.ec.dgc.gateway.connector.model.TrustListItem;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tng.trustnetwork.keydistribution.entity.SignerInformationEntity;
import tng.trustnetwork.keydistribution.repository.SignerInformationRepository;
import tng.trustnetwork.keydistribution.testdata.SignerInformationTestHelper;

@SpringBootTest
class SignerCertificateDownloadServiceTest {

    @MockBean
    DgcGatewayDownloadConnector dgcGatewayDownloadConnector;

    @Autowired
    SignerCertificateDownloadService signerCertificateDownloadService;

    @Autowired
    SignerInformationRepository signerInformationRepository;

    @Autowired
    SignerInformationTestHelper signerInformationTestHelper;

    @Test
    void downloadEmptyCertificatesList() {
        ArrayList<TrustListItem> trustList = new ArrayList<>();
        Mockito.when(dgcGatewayDownloadConnector.getTrustedCertificates()).thenReturn(trustList);

        signerCertificateDownloadService.downloadCertificates();

        List<SignerInformationEntity> repositoryItems = signerInformationRepository.findAllByDeletedOrderByIdAsc(false);
        Assertions.assertEquals(0, repositoryItems.size());
    }

    @Test
    void downloadCertificates() {
        ArrayList<TrustListItem> trustList = new ArrayList<>();
        trustList.add(signerInformationTestHelper.createTrustListItem(SignerInformationTestHelper.TEST_CERT_1_STR));
        Mockito.when(dgcGatewayDownloadConnector.getTrustedCertificates()).thenReturn(trustList);

        signerCertificateDownloadService.downloadCertificates();

        List<SignerInformationEntity> repositoryItems = signerInformationRepository.findAll();
        Assertions.assertEquals(1, repositoryItems.size());

        SignerInformationEntity repositoryItem = repositoryItems.get(0);
        Assertions.assertEquals(SignerInformationTestHelper.TEST_CERT_1_KID, repositoryItem.getKid());
        Assertions.assertEquals(SignerInformationTestHelper.TEST_CERT_1_STR, repositoryItem.getRawData());
    }
}

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

package tng.trustnetwork.keydistribution.service;

import java.util.List;
import eu.europa.ec.dgc.gateway.connector.DgcGatewayDownloadConnector;
import eu.europa.ec.dgc.gateway.connector.model.TrustListItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tng.trustnetwork.keydistribution.entity.TrustedPartyEntity;
import tng.trustnetwork.keydistribution.repository.TrustedPartyRepository;

@SpringBootTest
class TrustedPartyServiceTest {

    @MockBean
    DgcGatewayDownloadConnector dgcGatewayDownloadConnector;

    @Autowired
    TrustedPartyRepository trustedPartyRepository;

    @Autowired
    TrustedPartyService trustedPartyService;

    @BeforeEach
    void clearRepositoryData()  {
        trustedPartyRepository.deleteAll();
    }

    @Test
    void testUpdateCsca() {

        // Check DB is empty and create a dummy entity which will be deleted
        Assertions.assertEquals(0, trustedPartyRepository.count());

        trustedPartyRepository.save(TrustedPartyEntity.builder()
            .thumbprint("thumbprint_old")
            .country("CO")
            .rawData("raw_data_old")
            .type(TrustedPartyEntity.Type.CSCA)
            .build());

        // Build Test-Data
        TrustListItem trustListItem1 = new TrustListItem();
        trustListItem1.setThumbprint("thumb1");
        trustListItem1.setCountry("XX");
        trustListItem1.setSignature("sig1");
        trustListItem1.setRawData("raw1");

        TrustListItem trustListItem2 = new TrustListItem();
        trustListItem2.setThumbprint("thumb2");
        trustListItem2.setCountry("YY");
        trustListItem2.setSignature("sig2");
        trustListItem2.setRawData("raw2");
        List<TrustListItem> trustlist = List.of(trustListItem1, trustListItem2);

        // Pass Testdata to Service
        trustedPartyService.updateCscaFromTrustList(trustlist);

        // Existing TrustedParty should be deleted and the 2 new ones from testdata should be inserted
        Assertions.assertEquals(2, trustedPartyRepository.count());
        List<TrustedPartyEntity> persistedTrustedParties = trustedPartyRepository.findAll();

        Assertions.assertEquals(TrustedPartyEntity.Type.CSCA, persistedTrustedParties.get(0).getType());
        Assertions.assertEquals(trustListItem1.getCountry(), persistedTrustedParties.get(0).getCountry());
        Assertions.assertEquals(trustListItem1.getThumbprint(), persistedTrustedParties.get(0).getThumbprint());
        Assertions.assertEquals(trustListItem1.getRawData(), persistedTrustedParties.get(0).getRawData());

        Assertions.assertEquals(TrustedPartyEntity.Type.CSCA, persistedTrustedParties.get(1).getType());
        Assertions.assertEquals(trustListItem2.getCountry(), persistedTrustedParties.get(1).getCountry());
        Assertions.assertEquals(trustListItem2.getThumbprint(), persistedTrustedParties.get(1).getThumbprint());
        Assertions.assertEquals(trustListItem2.getRawData(), persistedTrustedParties.get(1).getRawData());

    }

}

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

import eu.europa.ec.dgc.gateway.connector.DgcGatewayDownloadConnector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tng.trustnetwork.keydistribution.entity.InfoEntity;
import tng.trustnetwork.keydistribution.repository.InfoRepository;
import java.util.List;

@SpringBootTest
class InfoServiceTest {

    @MockBean
    DgcGatewayDownloadConnector dgcGatewayDownloadConnector;

    @Autowired
    InfoRepository infoRepository;

    @Autowired
    InfoService infoService;

    @BeforeEach
    void clearRepositoryData()  {
        infoRepository.deleteAll();
    }

    @Test
    void saveInfo() throws Exception {

        infoService.setValueForKey("TestKey", "TestValue");

        List<InfoEntity> entities = infoRepository.findAll();

        Assertions.assertEquals(1, entities.size());
        Assertions.assertEquals("TestKey", entities.get(0).getIdentifierKey());
        Assertions.assertEquals("TestValue", entities.get(0).getValue());
    }

    @Test
    void getInfo() throws Exception {

        InfoEntity entity = new InfoEntity("TestKey", "TestValue");
        infoRepository.save(entity);

        Assertions.assertEquals("TestValue", infoService.getValueForKey("TestKey"));
    }
}
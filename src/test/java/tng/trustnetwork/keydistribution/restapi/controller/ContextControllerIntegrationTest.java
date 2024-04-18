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

package tng.trustnetwork.keydistribution.restapi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class ContextControllerIntegrationTest {

    @org.springframework.boot.test.mock.mockito.MockBean
    eu.europa.ec.dgc.gateway.connector.DgcGatewayDownloadConnector dgcGatewayDownloadConnector;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void requestContext() throws Exception {
        mockMvc.perform(get("/context"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(result -> assertContextStrEqualFile(result));
    }

    private void assertContextStrEqualFile(MvcResult result) throws UnsupportedEncodingException {
        String resultContext = result.getResponse().getContentAsString();
        Resource resource = new ClassPathResource("/static/context.json");
        String fileContext = null;
        try {
            fileContext = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            Assertions.fail(e);
        }
        Assertions.assertEquals(resultContext, fileContext);
    }

}

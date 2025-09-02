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

package tng.trustnetwork.keydistribution.service.did;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

public class TngAdditionalContextTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testTngAdditionalContextValidJson() throws Exception {
        // Test that the main additional context file is valid JSON
        InputStream inputStream = new ClassPathResource("did_contexts/tng-additional-context_v1.json").getInputStream();
        String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        
        JsonNode jsonNode = objectMapper.readTree(content);
        assertNotNull(jsonNode);
        assertTrue(jsonNode.has("@context"));
        
        JsonNode context = jsonNode.get("@context");
        assertTrue(context.has("participant"));
        assertTrue(context.has("keyusage"));
        assertTrue(context.has("domain"));
        assertTrue(context.has("kid"));
        assertTrue(context.has("JsonWebKey2020"));
    }

    @Test
    void testTngAdditionalContextDevValidJson() throws Exception {
        // Test that the DEV context file is valid JSON
        InputStream inputStream = new ClassPathResource("did_contexts/tng-additional-context-dev_v1.json").getInputStream();
        String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        
        JsonNode jsonNode = objectMapper.readTree(content);
        assertNotNull(jsonNode);
        assertTrue(jsonNode.has("@context"));
        
        JsonNode context = jsonNode.get("@context");
        assertTrue(context.has("participant"));
        assertTrue(context.has("keyusage"));
        assertTrue(context.has("domain"));
        
        // Verify DEV-specific URLs
        assertTrue(context.get("participant").get("@id").asText().contains("ValueSet-Participants-DEV"));
        assertTrue(context.get("keyusage").get("@id").asText().contains("ValueSet-KeyUsage-DEV"));
        assertTrue(context.get("domain").get("@id").asText().contains("ValueSet-Domain-DEV"));
    }

    @Test
    void testTngAdditionalContextUatValidJson() throws Exception {
        // Test that the UAT context file is valid JSON
        InputStream inputStream = new ClassPathResource("did_contexts/tng-additional-context-uat_v1.json").getInputStream();
        String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        
        JsonNode jsonNode = objectMapper.readTree(content);
        assertNotNull(jsonNode);
        assertTrue(jsonNode.has("@context"));
        
        JsonNode context = jsonNode.get("@context");
        assertTrue(context.has("participant"));
        assertTrue(context.has("keyusage"));
        assertTrue(context.has("domain"));
        
        // Verify UAT-specific URLs
        assertTrue(context.get("participant").get("@id").asText().contains("ValueSet-Participants-UAT"));
        assertTrue(context.get("keyusage").get("@id").asText().contains("ValueSet-KeyUsage-UAT"));
        assertTrue(context.get("domain").get("@id").asText().contains("ValueSet-Domain-UAT"));
    }

    @Test
    void testTngAdditionalContextProdValidJson() throws Exception {
        // Test that the PROD context file is valid JSON
        InputStream inputStream = new ClassPathResource("did_contexts/tng-additional-context-prod_v1.json").getInputStream();
        String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        
        JsonNode jsonNode = objectMapper.readTree(content);
        assertNotNull(jsonNode);
        assertTrue(jsonNode.has("@context"));
        
        JsonNode context = jsonNode.get("@context");
        assertTrue(context.has("participant"));
        assertTrue(context.has("keyusage"));
        assertTrue(context.has("domain"));
        
        // Verify PROD-specific URLs (smart.who.int domain)
        assertTrue(context.get("participant").get("@id").asText().contains("smart.who.int/trust/ValueSet-Participants"));
        assertTrue(context.get("keyusage").get("@id").asText().contains("smart.who.int/trust/ValueSet-KeyUsage"));
        assertTrue(context.get("domain").get("@id").asText().contains("smart.who.int/trust/ValueSet-Domain"));
    }

    @Test
    void testValueSetSchemasValidJson() throws Exception {
        // Test that all schema files are valid JSON
        String[] schemaFiles = {
            "valueset-keyusage.schema.json",
            "valueset-participants.schema.json", 
            "valueset-domain.schema.json",
            "valueset-keyusage-dev.schema.json",
            "valueset-participants-dev.schema.json",
            "valueset-domain-dev.schema.json",
            "valueset-keyusage-uat.schema.json",
            "valueset-participants-uat.schema.json",
            "valueset-domain-uat.schema.json",
            "valueset-keyusage-prod.schema.json",
            "valueset-participants-prod.schema.json",
            "valueset-domain-prod.schema.json"
        };
        
        for (String schemaFile : schemaFiles) {
            InputStream inputStream = new ClassPathResource("did_contexts/" + schemaFile).getInputStream();
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            
            JsonNode jsonNode = objectMapper.readTree(content);
            assertNotNull(jsonNode, "Schema file " + schemaFile + " should be valid JSON");
            assertTrue(jsonNode.has("$schema"), "Schema file " + schemaFile + " should have $schema property");
            assertTrue(jsonNode.has("$id"), "Schema file " + schemaFile + " should have $id property");
        }
    }
}
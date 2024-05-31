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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tng.trustnetwork.keydistribution.clients.UniversalResolverClient;
import tng.trustnetwork.keydistribution.model.DidDocument;


@Slf4j
@Service
@RequiredArgsConstructor
public class UniversalResolverService {

    private final UniversalResolverClient universalResolverClient;

    private final ObjectMapper objectMapper;

    /**
     * Try to resolve DID Document by ID at UniversalResolverService.
     *
     * @param didId Identifier of document to resolve
     * @return Parsed and RAW DID Document
     * @throws JsonProcessingException when parsing of downloaded document failed.
     */
    public DidDocumentWithRawResponse universalResolverApiCall(String didId) throws JsonProcessingException {

        String rawResponse = universalResolverClient.getDidDocument(didId);
        DidDocument didDocument = objectMapper.readValue(rawResponse, DidDocument.class);

        return new DidDocumentWithRawResponse(didDocument, rawResponse);
    }

    public record DidDocumentWithRawResponse(
        DidDocument didDocument,

        String raw) {
    }

}

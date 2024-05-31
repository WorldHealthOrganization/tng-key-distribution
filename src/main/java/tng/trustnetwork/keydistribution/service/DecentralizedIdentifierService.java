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

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tng.trustnetwork.keydistribution.entity.DecentralizedIdentifierEntity;
import tng.trustnetwork.keydistribution.mapper.DidMapper;
import tng.trustnetwork.keydistribution.model.DidDocument;
import tng.trustnetwork.keydistribution.repository.DecentralizedIdentifierRepository;
import tng.trustnetwork.keydistribution.repository.PublicKeyJwkRepository;
import tng.trustnetwork.keydistribution.repository.VerificationMethodRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class DecentralizedIdentifierService {

    private final DecentralizedIdentifierRepository decentralizedIdentifierRepository;

    private final VerificationMethodRepository verificationMethodRepository;

    private final PublicKeyJwkRepository publicKeyJwkRepository;

    private final DidMapper didMapper;

    /**
     * Update the list of Decentralized Identifier Documents.
     *
     * @param didDocument parsed DID Document
     * @param raw         RAW JSON Representation (This is required to be able to verify integrity of DID afterwords)
     */
    @Transactional
    public void updateDecentralizedIdentifierList(DidDocument didDocument, String raw) {

        DecentralizedIdentifierEntity didEntity = didMapper.toEntity(didDocument, raw);
        decentralizedIdentifierRepository.save(didEntity);

        didEntity.getVerificationMethods()
                 .stream()
                 .filter(Objects::nonNull)
                 .forEach(verificationMethod -> {

                     verificationMethod.setParentDocument(didEntity);
                     publicKeyJwkRepository.save(verificationMethod.getPublicKeyJwk());
                     verificationMethodRepository.save(verificationMethod);
                 });
    }
}

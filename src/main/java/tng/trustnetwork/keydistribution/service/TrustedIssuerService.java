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
import eu.europa.ec.dgc.gateway.connector.mapper.TrustedIssuerMapper;
import eu.europa.ec.dgc.gateway.connector.model.TrustedIssuer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tng.trustnetwork.keydistribution.config.KdsConfigProperties;
import tng.trustnetwork.keydistribution.entity.TrustedIssuerEntity;
import tng.trustnetwork.keydistribution.mapper.IssuerMapper;
import tng.trustnetwork.keydistribution.repository.TrustedIssuerRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrustedIssuerService {

    private final IssuerMapper issuerMapper;

    private final TrustedIssuerRepository trustedIssuerRepository;

    private final UniversalResolverService urService;

    private final DecentralizedIdentifierService decentralizedIdentifierService;

    private final KdsConfigProperties configProperties;

    /**
     * Method to query the db for DID documents.
     *
     * @return List holding the found trusted issuers.
     */
    public List<TrustedIssuerEntity> getAllDid() {

        return trustedIssuerRepository.findAllByUrlTypeIs(TrustedIssuerEntity.UrlType.DID);
    }

    /**
     * Method to synchronise the issuers in the db with the given List of trusted issuers.
     *
     * @param trustedIssuers defines the list of trusted issuers.
     */
    @Transactional
    public void updateTrustedIssuersList(List<TrustedIssuer> trustedIssuers) {

        trustedIssuerRepository.deleteAll();


        for (TrustedIssuer trustedIssuer : trustedIssuers) {

            trustedIssuerRepository.save(issuerMapper.trustedIssuerToTrustedIssuerEntity(trustedIssuer));

            if (trustedIssuer.getType() == TrustedIssuer.UrlType.DID) {
                resolveDid(trustedIssuer);
            }
        }
    }

    private void resolveDid(TrustedIssuer trustedIssuer) {

        if (!configProperties.getTrustedIssuerDownloader().isEnableTrustedIssuerResolving()) {
            return;
        }

        try {
            UniversalResolverService.DidDocumentWithRawResponse didDocument =
                urService.universalResolverApiCall(trustedIssuer.getUrl());

            decentralizedIdentifierService.updateDecentralizedIdentifierList(didDocument.didDocument(),
                                                                             didDocument.raw());
        } catch (JsonProcessingException e) {
            log.error("Failed to download/parse DID {}", trustedIssuer.getUrl());
        }
    }
}

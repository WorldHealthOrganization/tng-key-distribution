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

import eu.europa.ec.dgc.gateway.connector.model.TrustListItem;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tng.trustnetwork.keydistribution.entity.TrustedPartyEntity;
import tng.trustnetwork.keydistribution.repository.TrustedPartyRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrustedPartyService {


    private final TrustedPartyRepository trustedPartyRepository;

    /**
     * Insert given list of TrustListItems as CSCA into TrustedParty Table.
     * Table will be cleaned up previously.
     *
     * @param trustList List of trusted CSCA.
     */
    @Transactional
    public void updateCscaFromTrustList(List<TrustListItem> trustList) {

        trustedPartyRepository.deleteAllByType(TrustedPartyEntity.Type.CSCA);

        trustList.stream()
                 .map(this::getCscaEntity)
                 .forEach(trustedPartyRepository::save);
    }

    public List<TrustedPartyEntity> getCscaByCountry(String countryCode) {

        return trustedPartyRepository.findAllByCountryIsAndTypeIs(countryCode, TrustedPartyEntity.Type.CSCA);
    }

    private TrustedPartyEntity getCscaEntity(TrustListItem trustListItem) {

        return TrustedPartyEntity.builder()
                                 .country(trustListItem.getCountry())
                                 .rawData(trustListItem.getRawData())
                                 .type(TrustedPartyEntity.Type.CSCA)
                                 .build();
    }
}

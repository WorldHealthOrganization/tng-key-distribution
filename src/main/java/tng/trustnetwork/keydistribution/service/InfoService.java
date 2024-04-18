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

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tng.trustnetwork.keydistribution.entity.InfoEntity;
import tng.trustnetwork.keydistribution.repository.InfoRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class InfoService {

    public static final  String CURRENT_ETAG = "CURRENTETAG";

    private final InfoRepository infoRepository;

    /**
     *  Gets a value for the given key from the db.
     *
     * @param key the key, for which the value should be returned
     * @return the value or null if not found in db
     */
    public String getValueForKey(String key) {
        Optional<InfoEntity> optionalValue = infoRepository.findById(key);

        return optionalValue.map(InfoEntity::getValue).orElse(null);
    }

    /**
     * Saves the value for a given key in the db.
     *
     * @param key key of the value to save.
     * @param value the value to save
     */
    public void setValueForKey(String key, String value) {
        InfoEntity infoEntity = new InfoEntity(key, value);
        infoRepository.save(infoEntity);
    }
}

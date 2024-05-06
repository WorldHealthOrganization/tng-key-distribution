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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import tng.trustnetwork.keydistribution.config.KdsConfigProperties;

@ConditionalOnProperty(name = "dgc.did.didUploadProvider", havingValue = "dummy")
@Service
@Slf4j
@RequiredArgsConstructor
public class DummyGitUploader implements GitProvider {

    private final KdsConfigProperties configProperties;
    
    /**
     * upload dummy method used for unit tests.
     * @param sourcePath will only be used for log output
     */

    public void upload(String sourcePath) {
        
        log.info("Uploaded from {} to {}", sourcePath, configProperties.getDid().getGit().getWorkdir());

    }

}

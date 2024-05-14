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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import tng.trustnetwork.keydistribution.config.KdsConfigProperties;

@ConditionalOnProperty(name = "dgc.did.didUploadProvider", havingValue = "local-file")
@Service
@Slf4j
@RequiredArgsConstructor
public class LocalFileDidUploader implements DidUploader {

    private final KdsConfigProperties configProperties;

    @Override
    public void uploadDid(byte[] content) {

        uploadDid(null, content);
    }

    @Override
    public void uploadDid(String subContainer, byte[] content) {

        File targetFile;

        if (subContainer == null) {
            targetFile = Paths.get(
                configProperties.getDid().getLocalFile().getDirectory(),
                configProperties.getDid().getLocalFile().getFileName()
            ).toFile();
        } else {
            targetFile = Paths.get(
                configProperties.getDid().getLocalFile().getDirectory(),
                subContainer,
                configProperties.getDid().getLocalFile().getFileName()
            ).toFile();
        }

        if (targetFile.exists() && !targetFile.delete()) {
            log.error("Failed to delete existing file.");
            return;
        }

        if (content == null) {
            log.info("Requested to store file with null content - only deleting existing file");
            return;
        }

        log.info("Storing {} bytes to {}", content.length, targetFile.getAbsolutePath());

        if (targetFile.getParentFile().mkdirs()) {
            log.info("Created required directory {}", targetFile.getParentFile().getAbsolutePath());
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(targetFile)) {
            fileOutputStream.write(content);
        } catch (IOException e) {
            log.error("Failed to write DID Content to file: {}", e.getMessage());
            return;
        }

        log.info("Successfully saved file locally.");

    }

}

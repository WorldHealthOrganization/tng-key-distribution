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
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import tng.trustnetwork.keydistribution.config.KdsConfigProperties;

@ConditionalOnProperty(name = "dgc.did.didUploadProvider", havingValue = "local-file")
@Service
@Slf4j
@RequiredArgsConstructor
public class GitUploader implements GitProvider {

    private final KdsConfigProperties configProperties;

    /**
     * upload method clones a git repositor, copies the contents of sourcePath to the cloned git repository 
     * and commits and pushes the contents, replacing the previous contents of the repository.
     * @param sourcePath sourcePath from where the files are copied for upload 
     */

    public void upload(String sourcePath) {

        Path sourceDirectory = Paths.get(sourcePath);
        Path targetDirectory = Paths.get(configProperties.getDid().getGit().getWorkdir() 
          + File.separator 
          + configProperties.getDid().getGit().getPrefix());

        deleteDirectoryAndContents(configProperties.getDid().getGit().getWorkdir());

        try {
            Git.cloneRepository()
            .setURI(configProperties.getDid().getGit().getUrl())
            .setDirectory(new File(configProperties.getDid().getGit().getWorkdir()))
            .setCredentialsProvider(
                new UsernamePasswordCredentialsProvider(
                "anonymous", configProperties.getDid().getGit().getPat()))
                .call();
        } catch (Exception e) {
            log.error("Failed to clone repository {}: {}",
                      configProperties.getDid().getGit().getUrl(), e.getMessage());
        }

        try {
            // Copy and replace files from sourceDirectory to targetDirectory
            Files.walkFileTree(sourceDirectory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path targetFile = targetDirectory.resolve(sourceDirectory.relativize(file));
                    Files.createDirectories(targetFile.getParent());
                    Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path targetDir = targetDirectory.resolve(sourceDirectory.relativize(dir));
                    Files.createDirectories(targetDir);
                    return FileVisitResult.CONTINUE;
                }
            });

            //Delete files/folders in targetDirectory that are NOT in sourceDirectory
            Files.walkFileTree(targetDirectory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path relativePath = targetDirectory.relativize(file);
                    Path sourceFile = sourceDirectory.resolve(relativePath);
                    if (!Files.exists(sourceFile)) {
                        Files.delete(file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Path relativePath = targetDirectory.relativize(dir);
                    Path sourceDir = sourceDirectory.resolve(relativePath);
                    // Delete target dir if it doesn’t exist in source and is empty
                    if (!Files.exists(sourceDir)) {
                        try (DirectoryStream<Path> entries = Files.newDirectoryStream(dir)) {
                            if (!entries.iterator().hasNext()) {
                                Files.delete(dir);
                            }
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (IOException e) {
            log.error("Failed to copy files from {} to {}: {}", sourcePath, targetDirectory, e.getMessage());
        }

        try {
            Git git = Git.open(new File(configProperties.getDid().getGit().getWorkdir()));
            git.add().addFilepattern(".").call();
            git.commit().setMessage("Added DID files on " + Instant.now()).call();
            git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                "anonymous", configProperties.getDid().getGit().getPat())).call();
            git.close();
            log.info("Successfully uploaded DID files to Git repository {}",
                     configProperties.getDid().getGit().getUrl());
        } catch (GitAPIException | IOException e) {
            log.error("Error during Git commit & push: {}",e.getMessage());
        }
    }

    private void deleteDirectoryAndContents(String directoryPath) {
        Path dir = Paths.get(directoryPath);
        if (dir.toFile().exists()) {
    
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path path : stream) {
                    if (Files.isDirectory(path)) {
                        deleteDirectoryAndContents(path.toString());
                    } else {
                        Files.delete(path);
                    }
                }
            } catch (IOException e) {
                log.error("Error deleting file {}",e.getMessage());
            }
            try {
                Files.delete(dir);
            } catch (IOException e) {
                log.error("Error deleting root directory {}",e.getMessage());
            }
        } else {
            log.info("Directory {} does not exist, skippig deletion", dir);
        }
    }
}

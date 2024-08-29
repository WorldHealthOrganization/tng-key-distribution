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

import static tng.trustnetwork.keydistribution.service.did.KdsDidContextDocumentLoaderConfig.DID_CONTEXTS;

import com.apicatalog.jsonld.loader.DocumentLoader;
import com.danubetech.keyformats.crypto.ByteSigner;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.Base64URL;
import eu.europa.ec.dgc.utils.CertificateUtils;
import foundation.identity.jsonld.JsonLDException;
import foundation.identity.jsonld.JsonLDObject;
import info.weboftrust.ldsignatures.jsonld.LDSecurityKeywords;
import info.weboftrust.ldsignatures.signer.JsonWebSignature2020LdSigner;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tng.trustnetwork.keydistribution.config.KdsConfigProperties;
import tng.trustnetwork.keydistribution.entity.SignerInformationEntity;
import tng.trustnetwork.keydistribution.entity.TrustedIssuerEntity;
import tng.trustnetwork.keydistribution.service.KdsCertUtils;
import tng.trustnetwork.keydistribution.service.SignerInformationService;
import tng.trustnetwork.keydistribution.service.TrustedIssuerService;
import tng.trustnetwork.keydistribution.service.did.entity.DidTrustList;
import tng.trustnetwork.keydistribution.service.did.entity.DidTrustListEntry;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty("dgc.did.enableDidGeneration")
public class DidTrustListService {

    private static final String WILDCARD_CHAR = "-";

    private static final String SEPARATOR_DID_PATH = ":";

    private static final String SEPARATOR_DID_ID = "#";

    private final SignerInformationService signerInformationService;

    private final KdsConfigProperties configProperties;

    private final ByteSigner byteSigner;

    private final DidUploader didUploader;

    private final ObjectMapper objectMapper;

    private final CertificateUtils certificateUtils;

    private final TrustedIssuerService trustedIssuerService;

    private final GitProvider gitProvider;

    private final DocumentLoader documentLoader;

    private final KdsConfigProperties kdsConfigProperties;

    private final KdsCertUtils kdsCertUtils;

    @RequiredArgsConstructor
    @Getter
    private class DidSpecification {

        @Getter(AccessLevel.PRIVATE)
        private final List<String> path;

        private final Supplier<List<SignerInformationEntity>> certSupplier;

        private final Supplier<List<TrustedIssuerEntity>> issuerSupplier;

        private final Supplier<List<String>> didPathSupplier;

        public List<String> getPath(boolean ref) {
            ArrayList<String> path = new ArrayList<>(this.path);
            path.add(0, getListPathElement(ref));
            return path;
        }

        public String getDocumentId(boolean ref) {
            //Example: did:web:tng-cdn-dev.who.int:trustlist:v.2.0.0:DDCC:XXA:DSC
            return configProperties.getDid().getDidId()
                + SEPARATOR_DID_PATH + getListPathElement(ref)
                + (path.isEmpty() ? "" : SEPARATOR_DID_PATH
                + String.join(SEPARATOR_DID_PATH, path));
        }

        public String getEntryId(String kid) {
            //Example: did:web:tng-cdn-dev.who.int:trustlist:v.2.0.0:DDCC:XXA:DSC#kidkidkid
            return getDocumentId(false) + SEPARATOR_DID_ID + kid;
        }

        private String getListPathElement(boolean ref) {
            if (ref && configProperties.getDid().getTrustListRefPath() != null
                    && !configProperties.getDid().getTrustListRefPath().isEmpty()) {
                return configProperties.getDid().getTrustListRefPath();

            } else if (!ref && configProperties.getDid().getTrustListPath() != null
                && !configProperties.getDid().getTrustListPath().isEmpty()) {
                return configProperties.getDid().getTrustListPath();
            } else {
                return "";
            }
        }

        public String generateTrustListVerificationId(SignerInformationEntity signerInformationEntity) {

            String defaultPath = signerInformationEntity.getDomain()
                + SEPARATOR_DID_PATH + getParticipantCode(signerInformationEntity.getCountry())
                + SEPARATOR_DID_PATH + getMappedGroupName(signerInformationEntity.getGroup())
                + SEPARATOR_DID_PATH + encodeKid(signerInformationEntity.getKid());

            StringBuffer buf = new StringBuffer();
            String[] pathArray =  defaultPath.split(SEPARATOR_DID_PATH);

            for (int i = path.size(); i <= pathArray.length - 1; i++) {
                buf.append(SEPARATOR_DID_PATH + pathArray[i]);
            }

            return getDocumentId(false) + buf + SEPARATOR_DID_ID + encodeKid(signerInformationEntity.getKid());
        }
    }

    /**
     * Create and upload DID Document holding Uploaded DSC and Trusted Issuer.
     */
    @Scheduled(cron = "${dgc.did.cron}")
    @SchedulerLock(name = "didTrustListGenerator")
    public void job() {

        List<DidSpecification> didSpecifications = new ArrayList<>();
        List<String> domains = signerInformationService.getDomainsList();
        List<String> countries = signerInformationService.getCountryList();

        //CHECKSTYLE:OFF
        List<String> groups = signerInformationService.getGroupList();
        //CHECKSTYLE:ON

        // Add overall DID
        didSpecifications.add(new DidSpecification(
            Collections.emptyList(),
            signerInformationService::getAllCertificates,
            trustedIssuerService::getAllDid,
            () -> Stream.concat(Stream.of(WILDCARD_CHAR), domains.stream()).collect(Collectors.toList())
        ));

        // Add all Domain DID
        domains.forEach(domain -> {
            List<SignerInformationEntity> signerInformationEntitiesList =
                signerInformationService.getCertificatesByDomain(domain);
            didSpecifications.add(new DidSpecification(
                List.of(domain),
                () -> signerInformationEntitiesList,
                () -> trustedIssuerService.getAllDid(domain, null),
                () -> new ArrayList<>(
                    Stream.concat(Stream.of(WILDCARD_CHAR),signerInformationEntitiesList
                        .stream().map(entity -> getParticipantCode(entity.getCountry()))).collect(Collectors.toSet()))
            ));
        });

        // Add all Country and Domain specific DID
        domains.forEach(
            domain -> countries.forEach(country -> {
                List<SignerInformationEntity> signerInformationEntitiesList =
                    signerInformationService.getCertificatesByCountryDomain(country, domain);
                didSpecifications.add(new DidSpecification(
                    List.of(domain, getParticipantCode(country)),
                    () -> signerInformationEntitiesList,
                    () -> trustedIssuerService.getAllDid(domain, country),
                    () -> new ArrayList<>(
                        signerInformationEntitiesList.stream()
                          .filter(signerInformationEntity -> isDeniedGroup(signerInformationEntity.getGroup()))
                          .map(entity -> getMappedGroupName(entity.getGroup())).collect(Collectors.toSet()))));
            }));

        // Add all Country and Domain independant DID
        didSpecifications.add(new DidSpecification(
            List.of(WILDCARD_CHAR),
            () -> signerInformationService.getCertificatesByAllCountries(countries),
            Collections::emptyList,
            () -> new ArrayList<>(
                Stream.concat(Stream.of(WILDCARD_CHAR), countries.stream().map(country -> getParticipantCode(country)))
                      .collect(Collectors.toSet()))));



        // Add all Domain independent and country specific DID
        countries.forEach(
            country -> {
                List<SignerInformationEntity> signerInformationEntitiesList =
                    signerInformationService.getCertificatesByCountry(country);
                didSpecifications.add(new DidSpecification(
                    List.of(WILDCARD_CHAR, getParticipantCode(country)),
                    () -> signerInformationEntitiesList,
                    () -> trustedIssuerService.getAllDid(null, country),
                    () -> new ArrayList<>(
                        signerInformationEntitiesList.stream()
                            .filter(signerInformation -> isDeniedGroup(signerInformation.getGroup()))
                            .map(entity -> getMappedGroupName(entity.getGroup())).collect(Collectors.toSet()))
                ));
            });

        // Add all domain, country and group specific did
        domains.forEach(
            domain -> countries.forEach(
                country -> groups.forEach(
                    group -> {
                        List<SignerInformationEntity> signerInformationEntitiesList =
                            signerInformationService.getCertificatesByDomainParticipantGroup(domain, country, group);
                        didSpecifications.add(new DidSpecification(
                            List.of(domain, getParticipantCode(country), getMappedGroupName(group)),
                            () -> signerInformationEntitiesList,
                            Collections::emptyList,
                            () -> new ArrayList<>(
                                signerInformationEntitiesList.stream().map(entity -> encodeKid(entity.getKid()))
                                                             .collect(Collectors.toSet()))));
                    })));

        // Add all domain, country, group, kid specific did
        domains.forEach(
            domain -> countries.forEach(
                country -> groups.forEach(
                    group -> {
                        List<SignerInformationEntity> signerInformationEntitiesList =
                            signerInformationService.getCertificatesByDomainParticipantGroup(domain, country, group);

                        signerInformationEntitiesList.forEach(entity -> {
                            didSpecifications.add(new DidSpecification(
                                List.of(domain, getParticipantCode(country), getMappedGroupName(group),
                                        encodeKid(entity.getKid())),

                                () -> signerInformationService.getCertificatesByDomainParticipantGroupKid(
                                    domain, country, group, entity.getKid()),

                                Collections::emptyList,
                                Collections::emptyList
                            ));
                        });
                    }
                )));


        // Add all country and group specific did
        countries.forEach(
            country -> groups.forEach(
                group -> {
                    List<SignerInformationEntity> signerInformationEntitiesList =
                        signerInformationService.getCertificatesByGroupCountry(group, country);
                    didSpecifications.add(new DidSpecification(
                        List.of(WILDCARD_CHAR, getParticipantCode(country), getMappedGroupName(group)),
                        () -> signerInformationEntitiesList,
                        Collections::emptyList,
                        () -> new ArrayList<>(
                            signerInformationEntitiesList.stream().map(entity -> encodeKid(entity.getKid()))
                                                         .collect(Collectors.toSet()))));
                }));

        // Add all country, group, kid specific did
        countries.forEach(
            country -> groups.forEach(
                group -> {
                    List<SignerInformationEntity> signerInformationEntitiesList =
                        signerInformationService.getCertificatesByGroupCountry(group, country);

                    signerInformationEntitiesList.forEach(entity -> {

                        didSpecifications.add(new DidSpecification(
                            List.of(WILDCARD_CHAR, getParticipantCode(country), getMappedGroupName(group),
                                    encodeKid(entity.getKid())),

                            () -> signerInformationService.getCertificatesByKidGroupCountry(
                                country, group, entity.getKid()),

                            Collections::emptyList,
                            Collections::emptyList
                        ));
                    });
                }));

        // Add all domain and group specific did
        domains.forEach(
            domain -> groups.forEach(
                group -> {
                    List<SignerInformationEntity> signerInformationEntitiesList =
                        signerInformationService.getCertificatesByDomainGroup(domain, group);
                    didSpecifications.add(new DidSpecification(
                        List.of(domain, WILDCARD_CHAR, getMappedGroupName(group)),
                        () -> signerInformationEntitiesList,
                        Collections::emptyList,
                        () -> new ArrayList<>(
                            signerInformationEntitiesList.stream().map(entity -> encodeKid(entity.getKid()))
                                                         .collect(Collectors.toSet()))));

                }));

        // Add all country independent but domain and group specific did
        domains.forEach(
            domain -> {
                List<SignerInformationEntity> signerInformationEntitiesList =
                    signerInformationService.getCertificatesByDomain(domain);
                didSpecifications.add(new DidSpecification(
                    List.of(domain, WILDCARD_CHAR),
                    () -> signerInformationEntitiesList,
                    Collections::emptyList,
                    () -> new ArrayList<>(
                        signerInformationEntitiesList.stream()
                                                     .filter(domainEntity -> isDeniedGroup(domainEntity.getGroup()))
                                                     .map(domainEntity -> getMappedGroupName(domainEntity.getGroup()))
                                                     .collect(Collectors.toSet()))
                ));
            });



        // Add all domain, group and kid specific did
        domains.forEach(
            domain -> groups.forEach(
                group -> {
                    List<SignerInformationEntity> signerInformationEntitiesList =
                        signerInformationService.getCertificatesByDomainGroup(domain, group);
                    signerInformationEntitiesList.forEach(entity -> {
                        didSpecifications.add(new DidSpecification(
                            List.of(domain, WILDCARD_CHAR, getMappedGroupName(group),
                                    encodeKid(entity.getKid())),

                            () -> signerInformationService.getCertificatesByDomainGroupKid(
                                domain, group, entity.getKid()),

                            Collections::emptyList,
                            Collections::emptyList
                        ));
                    });
                }));



        // Add all group specific did
        groups.forEach(
            group -> {
                List<SignerInformationEntity> signerInformationEntitiesList =
                    signerInformationService.getCertificatesByGroup(group);
                didSpecifications.add(new DidSpecification(
                    List.of(WILDCARD_CHAR, WILDCARD_CHAR, getMappedGroupName(group)),
                    () -> signerInformationEntitiesList,
                    Collections::emptyList,
                    () -> new ArrayList<>(
                        signerInformationEntitiesList.stream()
                                                     .map(entity -> encodeKid(entity.getKid()))
                                                     .collect(Collectors.toSet()))));
            });

        // Add all domain  country independent and group specific did
        didSpecifications.add(new DidSpecification(
            List.of(WILDCARD_CHAR, WILDCARD_CHAR),
            () -> signerInformationService.getCertificatesByAllGroups(groups),
            Collections::emptyList,
            () -> new ArrayList<>(
                groups.stream().filter(group -> isDeniedGroup(group))
                      .map(group -> getMappedGroupName(group))
                      .collect(Collectors.toSet()))));

        // Add all group, kid specific did
        groups.forEach(
            group -> {
                List<SignerInformationEntity> signerInformationEntitiesList =
                    signerInformationService.getCertificatesByGroup(group);
                signerInformationEntitiesList.forEach(entity -> {
                    didSpecifications.add(new DidSpecification(
                        List.of(WILDCARD_CHAR, WILDCARD_CHAR, getMappedGroupName(group),
                                encodeKid(entity.getKid())),

                        () -> signerInformationService.getCertificatesByGroupKid(group, entity.getKid()),

                        Collections::emptyList,
                        Collections::emptyList
                    ));
                });
            }
        );


        Map<DidSpecification, String> didDocuments = new HashMap<>();
        didSpecifications.forEach(specification -> didDocuments
            .put(specification, this.generateTrustList(specification, false)));

        Map<DidSpecification, String> didRefDocuments = new HashMap<>();
        didSpecifications.forEach(specification -> didRefDocuments
            .put(specification, this.generateTrustList(specification, true)));

        didDocuments.forEach((specification, document) ->
                                 saveDid(String.join("/", specification.getPath(false)), document));

        didRefDocuments.forEach((specification, document) ->
                                    saveDid(String.join("/", specification.getPath(true)), document));

        log.info("Finished DID Export Process: {} documents", didDocuments.size());

        gitProvider.upload(configProperties.getDid().getLocalFile().getDirectory());

    }

    private void saveDid(String containerPath, String didDocument) {

        try {
            didUploader.uploadDid(containerPath,
                                  didDocument == null ? null : didDocument.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("Failed to Upload DID-TrustList: {}", e.getMessage());
        }
    }

    private String generateTrustList(DidSpecification specification, boolean onlyReferences) {

        List<SignerInformationEntity> signerInformationEntities = filterEntities(specification.getCertSupplier().get());
        List<TrustedIssuerEntity> trustedIssuerEntities = specification.getIssuerSupplier().get();
        List<String> didRefPathList =  specification.getDidPathSupplier().get();

        if (signerInformationEntities.isEmpty() && trustedIssuerEntities.isEmpty()) {
            log.info("Empty DID for path {}", specification.getPath());
            return null;
        }

        DidTrustList trustList = new DidTrustList();
        trustList.setContext(DID_CONTEXTS);
        trustList.setId(specification.getDocumentId(onlyReferences));
        trustList.setController(specification.getDocumentId(onlyReferences));
        trustList.setVerificationMethod(new ArrayList<>());

        // Add Certificates
        if (onlyReferences) {
            if (didRefPathList.isEmpty()) {
                trustList.getVerificationMethod().add(specification.getDocumentId(false));
            } else {
                didRefPathList.forEach(path -> {
                    trustList.getVerificationMethod()
                             .add(specification.getDocumentId(true) + SEPARATOR_DID_PATH + path);
                });
            }

            trustedIssuerEntities.forEach(did -> {
                if (!trustList.getVerificationMethod().contains(did.getUrl())) {
                    trustList.getVerificationMethod().add(did.getUrl());
                }
            });

        } else {
            for (SignerInformationEntity signerInformationEntity : signerInformationEntities) {

                X509Certificate parsedCertificate =
                    kdsCertUtils.parseCertificate(signerInformationEntity.getRawData());

                if (parsedCertificate == null) {
                    log.error("Could not parse cert {} of country {}",
                              signerInformationEntity.getKid(),
                              signerInformationEntity.getCountry());
                    return null;
                }

                PublicKey publicKey = parsedCertificate.getPublicKey();
                DidTrustListEntry.PublicKeyJwk publicKeyJwk = null;
                if (publicKey instanceof RSAPublicKey rsaPublicKey) {
                    publicKeyJwk = new DidTrustListEntry.RsaPublicKeyJwk(
                        rsaPublicKey, List.of(signerInformationEntity.getRawData()));

                } else if (publicKey instanceof ECPublicKey ecPublicKey) {
                    publicKeyJwk = new DidTrustListEntry.EcPublicKeyJwk(
                        ecPublicKey, List.of(signerInformationEntity.getRawData()));

                } else {
                    log.error("Public Key is not RSA or EC Public Key for cert {} of country {}",
                              signerInformationEntity.getKid(),
                              signerInformationEntity.getCountry());
                }

                addTrustListEntry(trustList, specification, signerInformationEntity, publicKeyJwk);
            }
        }


        // Sign Document
        JsonWebSignature2020LdSigner signer = new JsonWebSignature2020LdSigner(byteSigner);
        signer.setCreated(new Date());
        signer.setProofPurpose(LDSecurityKeywords.JSONLD_TERM_ASSERTIONMETHOD);
        signer.setVerificationMethod(URI.create(configProperties.getDid().getLdProofVerificationMethod()));
        signer.setDomain(configProperties.getDid().getLdProofDomain());
        signer.setNonce(generateNonce());


        try {
            JsonLDObject jsonLdObject = JsonLDObject.fromJson(objectMapper.writeValueAsString(trustList));
            jsonLdObject.setDocumentLoader(documentLoader);
            signer.sign(jsonLdObject);
            return jsonLdObject.toJson();
        } catch (IOException | GeneralSecurityException | JsonLDException e) {
            log.error("Failed to sign DID-TrustList: {}", e.getMessage());
            return null;
        }
    }

    private String getParticipantCode(String country) {

        if (country == null || country.length() != 2 && country.length() != 3) {
            return null;
        } else if (country.length() == 3) {
            return country.toUpperCase();
        }

        return configProperties.getDid().getVirtualCountries().computeIfAbsent(country, (c) -> {
            try {
                return new Locale("en", c).getISO3Country().toUpperCase();
            } catch (MissingResourceException e) {
                log.error("Country Code to alpha 3 conversion issue for country {} : {}",
                          c, e.getMessage());
                return c.toUpperCase();
            }
        });
    }

    private void addTrustListEntry(DidTrustList trustList,
                                   DidSpecification specification,
                                   SignerInformationEntity signerInformationEntity,
                                   DidTrustListEntry.PublicKeyJwk publicKeyJwk) {

        List<SignerInformationEntity> issuers = new ArrayList<>();
        searchIssuer(issuers, signerInformationEntity);

        issuers.forEach(issuer -> publicKeyJwk.getEncodedX509Certificates().add(issuer.getRawData()));

        DidTrustListEntry trustListEntry = new DidTrustListEntry();
        trustListEntry.setType("JsonWebKey2020");
        trustListEntry.setId(specification.generateTrustListVerificationId(signerInformationEntity));
        trustListEntry.setController(specification.getDocumentId(false));
        publicKeyJwk.setKid(encodeKid(signerInformationEntity.getKid()));
        trustListEntry.setPublicKeyJwk(publicKeyJwk);

        trustList.getVerificationMethod().add(trustListEntry);
    }


    private List<SignerInformationEntity> filterEntities(List<SignerInformationEntity> entities) {

        return entities.stream()
                       .filter(entity -> isDeniedGroup(entity.getGroup()))
                       .toList();
    }

    private String getMappedGroupName(String groupName) {

        return kdsConfigProperties.getDid().getGroupNameMapping()
                                  .computeIfAbsent(groupName, g -> g);
    }


    private boolean isDeniedGroup(String group) {
        return kdsConfigProperties.getDid().getGroupDenyList().stream()
                                  .noneMatch(e -> group.equalsIgnoreCase(e));
    }

    /**
     * Recursively resolve certificate chains based on current database.
     * Resolving is done country-code and domain aware.
     *
     * @param issuers List of SignerInformationEntity will be filled with found certs.
     *                Provide an empty List for initial call.
     * @param cert SignerInformationEntity to search issuers for.
     */
    private void searchIssuer(List<SignerInformationEntity> issuers, SignerInformationEntity cert) {

        try {
            X509Certificate parsedCertificate = kdsCertUtils.parseCertificate(cert.getRawData());
            String issuerSubjectHash = certificateUtils.calculateHash(parsedCertificate.getIssuerX500Principal()
                                                                                       .getEncoded());

            List<SignerInformationEntity> possibleIssuers = signerInformationService
                .getCertificatesBySubjectHashCountryDomain(issuerSubjectHash, cert.getCountry(), cert.getDomain());

            possibleIssuers.forEach(possibleIssuer -> {
                X509Certificate parsedPossibleIssuer = kdsCertUtils.parseCertificate(possibleIssuer.getRawData());

                if (parsedPossibleIssuer.equals(parsedCertificate)) {
                    // Self-signed Certificate detected --> Stopping Cert Chain resolving
                    return;
                }

                try {
                    parsedCertificate.verify(parsedPossibleIssuer.getPublicKey());
                    // Signature check passed --> Adding issuer to chain
                    issuers.add(possibleIssuer);
                    // Also try to resolve issuer cert
                    searchIssuer(issuers, possibleIssuer);

                } catch (Exception ignored) {
                    // Signature Check failed -> Do not add this issuer to chain
                }
            });
        } catch (NoSuchAlgorithmException ignored) {
            log.error("Failed to calculate Hash for Certificate Subject");
        }
    }

    private String generateNonce() {

        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        final int nonceLength = 32;
        StringBuilder nonce = new StringBuilder();

        while (nonce.length() < nonceLength) {
            nonce.append(chars.charAt((int) (Math.random() * chars.length())));
        }

        return nonce.toString();
    }

    private String encodeKid(String kid) {
        return Base64URL.encode(kid).toString();
    }
}

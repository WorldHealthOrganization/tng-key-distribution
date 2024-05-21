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
import eu.europa.ec.dgc.utils.CertificateUtils;
import foundation.identity.jsonld.JsonLDException;
import foundation.identity.jsonld.JsonLDObject;
import info.weboftrust.ldsignatures.jsonld.LDSecurityKeywords;
import info.weboftrust.ldsignatures.signer.JsonWebSignature2020LdSigner;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.bouncycastle.cert.X509CertificateHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tng.trustnetwork.keydistribution.config.KdsConfigProperties;
import tng.trustnetwork.keydistribution.entity.SignerInformationEntity;
import tng.trustnetwork.keydistribution.entity.TrustedIssuerEntity;
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

    @RequiredArgsConstructor
    @Getter
    private class DidSpecification {

        private final List<String> path;

        private final Supplier<List<SignerInformationEntity>> certSupplier;

        private final Supplier<List<TrustedIssuerEntity>> issuerSupplier;

        public String getDocumentId() {
            //Example: did:web:tng-cdn-dev.who.int:trustlist:v.2.0.0:DDCC:XXA:DSC
            return configProperties.getDid().getDidId()
                + (path.isEmpty() ? "" : SEPARATOR_DID_PATH
                + String.join(SEPARATOR_DID_PATH, path));
        }

        public String getEntryId(String kid) {
            //Example: did:web:tng-cdn-dev.who.int:trustlist:v.2.0.0:DDCC:XXA:DSC#kidkidkid
            return getDocumentId() + SEPARATOR_DID_ID + kid;
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

        // TODO: Add tng-cdn.who.int/trustlist/-/<PARTICIPANT_CODE>/<USAGE>
        //  (matches all domains for a specific participant and usage code)
        // TODO: Add tng-cdn.who.int/trustlist/<DOMAIN>/-/<USAGE> (matches all participants for a specific domain)

        // Add overall DID
        didSpecifications.add(new DidSpecification(
            Collections.emptyList(),
            signerInformationService::getAllCertificates,
            trustedIssuerService::getAllDid));

        // Add all Domain DID
        domains.forEach(
            domain -> didSpecifications.add(new DidSpecification(
                List.of(domain),
                () -> signerInformationService.getCertificatesByDomain(domain),
                trustedIssuerService::getAllDid)));

        // Add all Country and Domain specific DID
        domains.forEach(
            domain -> countries.forEach(
                country -> didSpecifications.add(new DidSpecification(
                    List.of(domain, getParticipantCode(country)),
                    () -> signerInformationService.getCertificatesByCountryDomain(country, domain),
                    trustedIssuerService::getAllDid)
                )));

        // Add all Domain independent and country specific DID
        countries.forEach(
            country -> didSpecifications.add(new DidSpecification(
                List.of(WILDCARD_CHAR, getParticipantCode(country)),
                () -> signerInformationService.getCertificatesByCountry(country),
                trustedIssuerService::getAllDid)));

        // Add all domain, country and group specific did
        domains.forEach(
            domain -> countries.forEach(
                country -> groups.forEach(
                    group -> didSpecifications.add(new DidSpecification(
                        List.of(domain, getParticipantCode(country), getMappedGroupName(group)),
                        () -> signerInformationService.getCertificatesByDomainParticipantGroup(domain, country, group),
                        trustedIssuerService::getAllDid)))));

        Map<DidSpecification, String> didDocuments = new HashMap<>();
        didSpecifications.forEach(specification -> didDocuments
            .put(specification, this.generateTrustList(specification)));

        didDocuments.forEach((specification, document) -> {
            saveDid(String.join("/", specification.getPath()), document);
        });

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

    private String generateTrustList(DidSpecification specification) {

        List<SignerInformationEntity> signerInformationEntities = filterEntities(specification.getCertSupplier().get());
        List<TrustedIssuerEntity> trustedIssuerEntities = specification.getIssuerSupplier().get();

        if (signerInformationEntities.isEmpty() || trustedIssuerEntities.isEmpty()) {
            log.info("Empty DID for path {}", specification.getPath());
            return null;
        }

        DidTrustList trustList = new DidTrustList();
        trustList.setContext(DID_CONTEXTS);
        trustList.setId(specification.getDocumentId());
        trustList.setController(specification.getDocumentId());
        trustList.setVerificationMethod(new ArrayList<>());

        // Add Certificates

        for (SignerInformationEntity signerInformationEntity : signerInformationEntities) {

            X509Certificate parsedCertificate = parseCertificate(signerInformationEntity.getRawData());
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

            addTrustListEntry(trustList, specification, signerInformationEntity, publicKeyJwk, parsedCertificate);
        }

        // Add Trusted Issuer (DID References)
        // TODO: Add filtering for TrustedIssuers
        trustedIssuerEntities.forEach(did -> trustList.getVerificationMethod().add(did.getUrl()));

        // Create LD-Proof Document
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

    private X509Certificate parseCertificate(String raw) {

        try {
            byte[] rawDataBytes = Base64.getDecoder().decode(raw);
            X509CertificateHolder certificateHolder = new X509CertificateHolder(rawDataBytes);
            return certificateUtils.convertCertificate(certificateHolder);
        } catch (CertificateException | IOException e) {
            return null;
        }
    }

    private void addTrustListEntry(DidTrustList trustList,
                                   DidSpecification specification,
                                   SignerInformationEntity signerInformationEntity,
                                   DidTrustListEntry.PublicKeyJwk publicKeyJwk,
                                   X509Certificate dsc) {

        // TODO: Add Logic to resolve issuer-relationships within our cached trustlist.
        Optional<X509Certificate> issuer = searchCsca(dsc, signerInformationEntity.getCountry());

        if (issuer.isPresent()) {
            try {
                String encodedCsca = Base64.getEncoder().encodeToString(issuer.get().getEncoded());
                publicKeyJwk.getEncodedX509Certificates()
                            .add(encodedCsca);
            } catch (CertificateEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        DidTrustListEntry trustListEntry = new DidTrustListEntry();
        trustListEntry.setType("JsonWebKey2020");
        trustListEntry.setId(specification.getEntryId(
            URLEncoder.encode(signerInformationEntity.getKid(), StandardCharsets.UTF_8)));
        trustListEntry.setController(specification.getDocumentId());
        trustListEntry.setPublicKeyJwk(publicKeyJwk);

        trustList.getVerificationMethod().add(trustListEntry);
    }


    private List<SignerInformationEntity> filterEntities(List<SignerInformationEntity> entities) {

        return entities.stream()
                       .filter(entity -> kdsConfigProperties.getDid().getGroupDenyList().stream()
                                                            .noneMatch(e -> entity.getGroup().equalsIgnoreCase(e)))
                       .toList();
    }

    private String getMappedGroupName(String groupName) {

        return kdsConfigProperties.getDid().getGroupNameMapping()
                                  .computeIfAbsent(groupName, g -> g);
    }

    /**
     * Search for CSCA for DSC.
     *
     * @param dsc DSC to search CSCA for.
     * @return Optional holding the CSCA if found.
     */
    private Optional<X509Certificate> searchCsca(X509Certificate dsc, String country) {

        return Optional.empty();
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

}

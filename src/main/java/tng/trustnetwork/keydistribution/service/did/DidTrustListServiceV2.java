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

import com.apicatalog.jsonld.document.JsonDocument;
import com.danubetech.keyformats.crypto.ByteSigner;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.dgc.utils.CertificateUtils;
import foundation.identity.jsonld.ConfigurableDocumentLoader;
import foundation.identity.jsonld.JsonLDObject;
import info.weboftrust.ldsignatures.jsonld.LDSecurityKeywords;
import info.weboftrust.ldsignatures.signer.JsonWebSignature2020LdSigner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.bouncycastle.cert.X509CertificateHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tng.trustnetwork.keydistribution.config.KdsConfigProperties;
import tng.trustnetwork.keydistribution.entity.SignerInformationEntity;
import tng.trustnetwork.keydistribution.service.SignerInformationService;
import tng.trustnetwork.keydistribution.service.TrustedIssuerService;
import tng.trustnetwork.keydistribution.service.TrustedPartyService;
import tng.trustnetwork.keydistribution.service.did.entity.DidTrustList;
import tng.trustnetwork.keydistribution.service.did.entity.DidTrustListEntry;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty("dgc.did.enableDidGeneration")
public class DidTrustListServiceV2 {

    private static final String SEPARATOR_COLON = ":";

    private static final String SEPARATOR_FRAGMENT = "#";

    private static final List<String> DID_CONTEXTS = List.of(
        "https://www.w3.org/ns/did/v1",
        "https://w3id.org/security/suites/jws-2020/v1");
    private static final String TYPE_DOMAIN = "D"; //domain
    private static final String TYPE_PARTICIPANT = "P"; //participatn (aka country code)
    private static final String TYPE_CERTIFICATE = "C"; //certificate type (DSC; SCA)

    private final TrustedPartyService trustedPartyService;

    private final SignerInformationService signerInformationService;

    private final KdsConfigProperties configProperties;

    private final ByteSigner byteSigner;

    private final DidUploader didUploader;

    private final ObjectMapper objectMapper;

    private final CertificateUtils certificateUtils;

    private final TrustedIssuerService trustedIssuerService;
    
    private final GitProvider gitProvider;

    /**
     * Create and upload DID Document holding Uploaded DSC and Trusted Issuer.
     */
    @Scheduled(cron = "${dgc.did.cron}")
    @SchedulerLock(name = "didTrustListGeneratorV2")
    public void job() {


        // for all domains
        for (String domain : signerInformationService.getDomainsList()) {

            try {
                //generate + save DID for domain
                saveDid(generateContainerPathForDid(domain, null, null), generateTrustList(domain, null, null));
            } catch (Exception e) {
                log.error("Failed to process DID-TrustList for domain {} : {}", domain, e.getMessage());
            }


            for (String participant : signerInformationService.getParticipantsByDomain(domain)) {
                String didDocument = null;
                try {
                    saveDid(generateContainerPathForDid(domain, participant, null), generateTrustList(domain, participant, null));
                    //saveDid(generateContainerPathForDid(domain, participant, DSC), generateTrustList(domain, participant, DSC));
                    //saveDid(generateContainerPathForDid(domain, participant, CSCA), generateTrustList(domain, participant, CSCA));
                } catch (Exception e) {
                    log.error("Failed to process DID-TrustList for domain {} : {}", domain, e.getMessage());
                }
            }

        }

        log.info("Finished DID Export Process");

        //gitProvider.upload(configProperties.getDid().getLocalFile().getDirectory());

    }



    private void saveDid(String containerPath, String didDocument){
        try {
            didUploader.uploadDid(containerPath, didDocument.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("Failed to Upload DID-TrustList: {}", e.getMessage());
        }
    }

    private String generateTrustList(String domain, String participant, String certificateType) throws Exception {

        DidTrustList trustList = new DidTrustList();
        trustList.setContext(DID_CONTEXTS);
        trustList.setId(generateDidId(domain, participant, certificateType, null));
        trustList.setController(generateDidId(domain, participant, certificateType, null));
        trustList.setVerificationMethod(new ArrayList<>());


        // Add DSC
        List<SignerInformationEntity> signerInformationEntities = getSignerInformationEntities(domain, participant);

        for (SignerInformationEntity signerInformationEntity : signerInformationEntities) {

            X509Certificate parsedCertificate = parseCertificate(signerInformationEntity.getRawData());
            PublicKey publicKey = parsedCertificate.getPublicKey();

            if (publicKey instanceof RSAPublicKey rsaPublicKey) {
                //TODO: refactor to set TrustListEntry id based on parameters and kid
                addTrustListEntry(trustList, signerInformationEntity,
                                  new DidTrustListEntry.RsaPublicKeyJwk(
                                      rsaPublicKey, List.of(signerInformationEntity.getRawData())), parsedCertificate);

            } else if (publicKey instanceof ECPublicKey ecPublicKey) {
                //TODO: refactor to set TrustListEntry id based on parameters and kid
                addTrustListEntry(trustList, signerInformationEntity,
                                  new DidTrustListEntry.EcPublicKeyJwk(
                                      ecPublicKey, List.of(signerInformationEntity.getRawData())), parsedCertificate);

            } else {
                log.error("Public Key is not RSA or EC Public Key for cert {} of country {}",
                          signerInformationEntity.getKid(),
                          signerInformationEntity.getCountry());
            }
        }

        // Add DID References
        trustedIssuerService.getAllDid()
                            .forEach(did -> trustList.getVerificationMethod().add(did.getUrl()));

        // Create LD-Proof Document
        JsonWebSignature2020LdSigner signer = new JsonWebSignature2020LdSigner(byteSigner);
        signer.setCreated(new Date());
        signer.setProofPurpose(LDSecurityKeywords.JSONLD_TERM_ASSERTIONMETHOD);
        signer.setVerificationMethod(URI.create(configProperties.getDid().getLdProofVerificationMethod()));
        signer.setDomain(configProperties.getDid().getLdProofDomain());
        signer.setNonce(configProperties.getDid().getLdProofNonce());

        // Load DID-Contexts
        Map<URI, JsonDocument> contextMap = new HashMap<>();
        for (String didContext : DID_CONTEXTS) {
            String didContextFile = configProperties.getDid().getContextMapping().get(didContext);

            if (didContextFile == null) {
                log.error("Failed to load DID-Context Document for {}: No Mapping to local JSON-File.", didContext);
            }

            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(
                "did_contexts/" + didContextFile)) {
                if (inputStream != null) {
                    contextMap.put(URI.create(didContext), JsonDocument.of(inputStream));
                }
            } catch (Exception e) {
                log.error("Failed to load DID-Context Document {}: {}", didContextFile, e.getMessage());
                throw e;
            }
        }
        JsonLDObject jsonLdObject = JsonLDObject.fromJson(objectMapper.writeValueAsString(trustList));
        jsonLdObject.setDocumentLoader(new ConfigurableDocumentLoader(contextMap));

        signer.sign(jsonLdObject);

        return jsonLdObject.toJson();
    }





    private List<SignerInformationEntity> getSignerInformationEntities(String domain, String participant) {
        return signerInformationService.getActiveCertificatesForFilter(domain, participant);
    }



    private String generateDidId(String domain, String participant, String certificateType, String kid) {

        //Example: did:web:tng-cdn-dev.who.int:trustlist:v.2.0.0:DDCC:XXA:DSC

        StringBuilder idBuilder = new StringBuilder(configProperties.getDid().getDidId());

        if (domain != null && !domain.isEmpty()) {
            idBuilder.append(SEPARATOR_COLON);
            idBuilder.append(domain);
            if (participant != null && !participant.isEmpty()) {
                if (idBuilder.length() > configProperties.getDid().getDidId().length()) {
                    idBuilder.append(SEPARATOR_COLON);
                }
                idBuilder.append(participant);
                if (certificateType != null && !certificateType.isEmpty()) {
                    if (idBuilder.length() > configProperties.getDid().getDidId().length()) {
                        idBuilder.append(SEPARATOR_COLON);
                    }
                    idBuilder.append(certificateType);
                    if (kid != null && !kid.isEmpty()) {
                        if (idBuilder.length() > configProperties.getDid().getDidId().length()) {
                            idBuilder.append(SEPARATOR_COLON);
                        }
                        idBuilder.append(kid);
                    }
                }
            }
        }
        //Note: the generated ID should match the path the resuting file is served at (check: getContainerPathForDid)
        return idBuilder.toString();
    }

    private String generateContainerPathForDid(String domain, String participant, String certificateType) {
        StringBuilder path = new StringBuilder();
        if (domain != null) {
            path.append(domain);
        }
        if (participant != null) {
            if (path.length() > 0) {
                path.append("/");
            }
            path.append(participant);
        }
        if (certificateType != null) {
            if (path.length() > 0) {
                path.append("/");
            }
            path.append(certificateType);
        }
        return path.toString();
    }

    //writeDidLocal(Path, DidDoc)



    private String getCountryAsLowerCaseAlpha3(String country) {

        if (country == null || country.length() != 2 && country.length() != 3) {
            return null;
        } else if (country.length() == 3) {
            return country;
        }

        return configProperties.getDid().getVirtualCountries().computeIfAbsent(country, (c) -> {
            try {
                return new Locale("en", c).getISO3Country().toLowerCase();
            } catch (MissingResourceException e) {
                log.error("Country Code to alpha 3 conversion issue for country {} : {}",
                          c, e.getMessage());
                return c;
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
                                   SignerInformationEntity signerInformationEntity,
                                   DidTrustListEntry.PublicKeyJwk publicKeyJwk,
                                   X509Certificate dsc) {

        Optional<X509Certificate> csca = searchCsca(dsc, signerInformationEntity.getCountry());

        if (csca.isPresent()) {

            try {
                String encodedCsca = Base64.getEncoder().encodeToString(csca.get().getEncoded());
                publicKeyJwk.getEncodedX509Certificates()
                            .add(encodedCsca);
            } catch (CertificateEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        DidTrustListEntry trustListEntry = new DidTrustListEntry();
        trustListEntry.setType("JsonWebKey2020");
        trustListEntry.setId(configProperties.getDid().getTrustListIdPrefix()
                                 + SEPARATOR_COLON
                                 + getCountryAsLowerCaseAlpha3(signerInformationEntity.getCountry())
                                 + SEPARATOR_FRAGMENT
                                 + URLEncoder.encode(signerInformationEntity.getKid(), StandardCharsets.UTF_8));
        trustListEntry.setController(configProperties.getDid().getTrustListControllerPrefix()
                                         + SEPARATOR_COLON
                                         + getCountryAsLowerCaseAlpha3(signerInformationEntity.getCountry()));
        trustListEntry.setPublicKeyJwk(publicKeyJwk);

        trustList.getVerificationMethod().add(trustListEntry);
    }

    /**
     * Search for CSCA for DSC.
     *
     * @param dsc DSC to search CSCA for.
     * @return Optional holding the CSCA if found.
     */
    private Optional<X509Certificate> searchCsca(X509Certificate dsc, String country) {

        return trustedPartyService.getCscaByCountry(country)
                                  .stream()
                                  .map(csca -> parseCertificate(csca.getRawData()))//TODO: CSCA for filter: domain, participant
                                  .filter(Objects::nonNull)
                                  .filter(csca -> csca.getSubjectX500Principal()
                                                      .equals(dsc.getIssuerX500Principal()))
                                  .findFirst();
    }

}

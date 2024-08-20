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

import eu.europa.ec.dgc.gateway.connector.model.TrustedCertificateTrustListItem;
import eu.europa.ec.dgc.utils.CertificateUtils;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tng.trustnetwork.keydistribution.entity.SignerInformationEntity;
import tng.trustnetwork.keydistribution.repository.SignerInformationRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignerInformationService {

    private final SignerInformationRepository signerInformationRepository;
    private final CertificateUtils certificateUtils;
    private final KdsCertUtils kdsCertUtils;

    /**
     * Update stored certificates with given list of new certificates.
     *
     * @param trustedCerts defines the list of trusted certificates.
     */
    @Transactional
    public void updateTrustedCertsList(List<TrustedCertificateTrustListItem> trustedCerts) {

        signerInformationRepository.deleteAll();

        trustedCerts.stream()
                    .map(this::getSignerInformationEntity)
                    .forEach(signerInformationRepository::save);
    }

    private SignerInformationEntity getSignerInformationEntity(TrustedCertificateTrustListItem cert) {

        SignerInformationEntity signerEntity = new SignerInformationEntity();
        signerEntity.setKid(cert.getKid());
        signerEntity.setCreatedAt(ZonedDateTime.now());
        signerEntity.setCountry(cert.getCountry());
        signerEntity.setRawData(cert.getCertificate());
        signerEntity.setDomain(cert.getDomain());
        signerEntity.setGroup(cert.getGroup());

        try {
            X509Certificate parsedCertificate = kdsCertUtils.parseCertificate(cert.getCertificate());
            byte[] subjectBytes = parsedCertificate.getSubjectX500Principal().getEncoded();
            signerEntity.setSubjectHash(certificateUtils.calculateHash(subjectBytes));
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to calculate Hash for certificate {}", cert.getKid());
        }

        return signerEntity;
    }

    /**
     * Returns a list of 2-Digit Country-Codes which have at least one signing certificates present in DB.
     *
     * @return Distinct list of Country-Codes
     */
    public List<String> getCountryList() {

        return signerInformationRepository.getCountryList();
    }

    /**
     * Returns a list of groups for which certificates are imported.
     *
     * @return list of groups
     */
    public List<String> getGroupList() {

        return signerInformationRepository.getGroupList();
    }

    /**
     * Returns a list of domains for which certificates are imported.
     *
     * @return list of domains
     */
    public List<String> getDomainsList() {

        return signerInformationRepository.getDomainsList();
    }

    /**
     * Returns a list of all certificates.
     *
     * @return List of SignerInformationEntity
     */
    public List<SignerInformationEntity> getAllCertificates() {

        return signerInformationRepository.findAll();
    }

    /**
     * Returns signer information that are active filtered by domain, participant and group.
     *
     * @param domain      a domain name used as filter
     * @param participant a participant aka country code, used as filter
     * @param group       group name, used as filter
     * @return matching SignerInformationEntities
     */
    public List<SignerInformationEntity> getCertificatesByDomainParticipantGroup(
        String domain, String participant, String group) {

        return signerInformationRepository.getByDomainIsAndCountryIsAndGroupIs(domain, participant, group);
    }


    public List<SignerInformationEntity> getCertificatesByDomainParticipantGroupKid(
        String domain, String participant, String group, String kid) {

        return signerInformationRepository.getByDomainIsAndCountryIsAndGroupIsAndKidIs(domain, participant, group, kid);
    }

    /**
     * Returns signer information that are filtered by participant.
     *
     * @param country a participant aka country code, used as filter
     * @return matching SignerInformationEntities
     */
    public List<SignerInformationEntity> getCertificatesByCountry(String country) {

        return signerInformationRepository.getByCountryIs(country);
    }

    public List<SignerInformationEntity> getCertificatesByAllCountries(List<String>  countries) {

        return signerInformationRepository.findByCountryIn(countries);
    }

    public List<SignerInformationEntity> getCertificatesByAllGroups(List<String>  groups) {

        return signerInformationRepository.findByGroupIn(groups);
    }
    /**
     * Returns signer information that are filtered by domain and participant.
     *
     * @param domain      a domain name used as filter
     * @param country     a participant aka country code, used as filter
     * @return matching SignerInformationEntities
     */
    public List<SignerInformationEntity> getCertificatesByCountryDomain(String country, String domain) {

        return signerInformationRepository.getByDomainIsAndCountryIs(domain, country);
    }

    /**
     * Returns signer information that are filtered by domain.
     *
     * @param domain      a domain name used as filter
     * @return matching SignerInformationEntities
     */
    public List<SignerInformationEntity> getCertificatesByDomain(String domain) {

        return signerInformationRepository.getByDomainIs(domain);
    }

    /**
     * Returns signer information that are filtered by participant and group.
     *
     * @param group       group name, used as filter
     * @param country a participant aka country code, used as filter
     * @return matching SignerInformationEntities
     */
    public List<SignerInformationEntity> getCertificatesByGroupCountry(String group, String country) {

        return signerInformationRepository.getByCountryIsAndGroupIs(country, group);
    }

    public List<SignerInformationEntity> getCertificatesByKidGroupCountry(String country, String group, String kid) {

        return signerInformationRepository.getByCountryIsAndGroupIsAndKidIs(country, group, kid);
    }

    /**
     * Returns signer information that are filtered by domain and group.
     *
     * @param domain      a domain name used as filter
     * @param group       group name, used as filter
     * @return matching SignerInformationEntities
     */
    public List<SignerInformationEntity> getCertificatesByDomainGroup(String domain, String group) {

        return signerInformationRepository.getByDomainIsAndGroupIs(domain, group);
    }


    public List<SignerInformationEntity> getCertificatesByDomainGroupKid(String domain, String group, String kid) {

        return signerInformationRepository.getByDomainIsAndGroupIsAndKidIs(domain, group, kid);
    }

    /**
     * Returns signer information that are filtered by group.
     *
     * @param group       group name, used as filter
     * @return matching SignerInformationEntities
     */
    public List<SignerInformationEntity> getCertificatesByGroup(String group) {

        return signerInformationRepository.getByGroupIs(group);
    }

    public List<SignerInformationEntity> getCertificatesByGroupKid(String group, String kid) {

        return signerInformationRepository.getByGroupIsAndKidIs(group, kid);
    }

    /**
     * Returns signer information that are filtered by subjectHash, country, and domain.
     *
     * @param subjectHash SHA256 hash of certificate subject to filter
     * @param country CountryCode/Participant code to filter
     * @param domain Domain value to filter for
     * @return matching SignerInformationEntities
     */
    public List<SignerInformationEntity> getCertificatesBySubjectHashCountryDomain(String subjectHash, String country,
                                                                                   String domain) {

        return signerInformationRepository.getBySubjectHashIsAndCountryIsAndDomainIs(subjectHash, country, domain);
    }
}

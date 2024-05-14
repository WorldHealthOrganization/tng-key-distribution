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

        return signerEntity;
    }

    /**
     * Returns a list of 2-Digit Country-Codes which have at least one signing certificates present in DB which is not
     * marked for deletion.
     *
     * @return Distinct list of Country-Codes
     */
    public List<String> getCountryList() {

        return signerInformationRepository.getCountryList();
    }

    public List<String> getGroupList() {

        return signerInformationRepository.getGroupList();
    }

    /**
     * Returns a list of all active certificates.
     *
     * @return List of SignerInformationEntity
     */
    public List<SignerInformationEntity> getAllCertificates() {

        return signerInformationRepository.findAll();
    }

    /**
     * Returns a list of active certificates for given list of countries.
     *
     * @param countries List of Country Codes to filter for.     *
     * @return List of SignerInformationEntity
     */
    public List<SignerInformationEntity> getCertificatesForCountries(List<String> countries) {

        return signerInformationRepository.getByCountryIsIn(countries);
    }

    /**
     * Returns signer information that are active filtered by domain and participant.
     *
     * @param domain      a domain name used as filter
     * @param participant a participant aka country code, used as filter
     * @return active signer information
     */
    public List<SignerInformationEntity> getCertificatesForFilter(String domain, String participant) {

        if (domain != null && participant != null) {
            return signerInformationRepository.getByDomainIsAndCountryIs(domain, participant);
        } else if (domain != null) {
            return signerInformationRepository.getByDomainIs(domain);
        } else {
            return getAllCertificates();
        }
    }

    /**
     * Returns signer information that are active filtered by domain, participant and group.
     *
     * @param domain      a domain name used as filter
     * @param participant a participant aka country code, used as filter
     * @param group       group name, used as filter
     * @return active signer information
     */
    public List<SignerInformationEntity> getCertificatesByDomainParticipantGroup(
        String domain, String participant, String group) {

        return signerInformationRepository.getByDomainIsAndCountryIsAndGroupIs(domain, participant, group);
    }

    public List<SignerInformationEntity> getCertificatesByCountry(String country) {

        return signerInformationRepository.getByCountryIs(country);
    }

    public List<SignerInformationEntity> getCertificatesByCountryDomain(String country, String domain) {

        return signerInformationRepository.getByDomainIsAndCountryIs(domain, country);
    }

    public List<SignerInformationEntity> getCertificatesByDomain(String domain) {

        return signerInformationRepository.getByDomainIs(domain);
    }

    public List<SignerInformationEntity> getCertificatesByGroupCountry(String group, String country) {

        return signerInformationRepository.getByCountryIsAndGroupIs(country, group);
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
     * Returns a list of participants filtered by domain.
     *
     * @param domain a domain name used as filter
     * @return list of participants
     */
    public List<String> getParticipantsByDomain(String domain) {

        return signerInformationRepository.getParticipantsByDomain(domain);
    }
}

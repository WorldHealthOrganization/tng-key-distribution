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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tng.trustnetwork.keydistribution.entity.SignerInformationEntity;
import tng.trustnetwork.keydistribution.repository.SignerInformationRepository;
import tng.trustnetwork.keydistribution.restapi.dto.CertificatesLookupResponseItemDto;
import tng.trustnetwork.keydistribution.restapi.dto.DeltaListDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignerInformationService {

    private final SignerInformationRepository signerInformationRepository;

    /**
     * Method to query the db for a certificate with a resume token.
     *
     * @param resumeToken defines which certificate should be returned.
     * @return Optional holding the certificate if found.
     */
    public Optional<SignerInformationEntity> getCertificate(Long resumeToken) {

        if (resumeToken == null) {
            return signerInformationRepository.findFirstByIdIsNotNullAndDeletedOrderByIdAsc(false);
        } else {
            return signerInformationRepository.findFirstByIdGreaterThanAndDeletedOrderByIdAsc(resumeToken, false);
        }
    }

    /**
     * Method to query the db for a list of kid from all certificates.
     *
     * @return A list of kids of all certificates found. If no certificate was found an empty list is returned.
     */
    public List<String> getListOfValidKids() {

        List<SignerInformationEntity> certsList = signerInformationRepository.findAllByDeletedOrderByIdAsc(false);

        return certsList.stream().map(SignerInformationEntity::getKid).collect(Collectors.toList());

    }

    /**
     * Method to synchronise the certificates in the db with the given List of trusted certificates.
     *
     * @param trustedCerts defines the list of trusted certificates.
     */
    @Transactional
    public void updateTrustedCertsList(List<TrustedCertificateTrustListItem> trustedCerts) {

        List<String> trustedCertsKids = trustedCerts.stream().map(
            TrustedCertificateTrustListItem::getKid).collect(Collectors.toList());
        List<String> alreadyStoredCerts = getListOfValidKids();
        List<String> certsToDelete = new ArrayList<>();

        if (trustedCertsKids.isEmpty()) {
            signerInformationRepository.setAllDeleted();
            return;
        } else {
            signerInformationRepository.setDeletedByKidsNotIn(trustedCertsKids);
        }

        List<SignerInformationEntity> signerInformationEntities = new ArrayList<>();

        for (TrustedCertificateTrustListItem cert : trustedCerts) {
            if (!alreadyStoredCerts.contains(cert.getKid())) {
                signerInformationEntities.add(getSignerInformationEntity(cert));
                certsToDelete.add(cert.getKid());
            }
        }

        //Delete all certificates that got updated, so that they get a new id.
        signerInformationRepository.deleteByKidIn(certsToDelete);
        signerInformationRepository.saveAllAndFlush(signerInformationEntities);
    }

    private SignerInformationEntity getSignerInformationEntity(TrustedCertificateTrustListItem cert) {

        SignerInformationEntity signerEntity = new SignerInformationEntity();
        signerEntity.setKid(cert.getKid());
        signerEntity.setCreatedAt(ZonedDateTime.now());
        signerEntity.setCountry(cert.getCountry());
        signerEntity.setRawData(cert.getCertificate());
        signerEntity.setDomain(cert.getDomain());

        return signerEntity;
    }

    /**
     * Gets the deleted/updated state of the certificates.
     *
     * @return state of the certificates represented by their kids
     */
    public DeltaListDto getDeltaList() {

        List<SignerInformationEntity> certs =
            signerInformationRepository.findAllByOrderByIdAsc();

        Map<Boolean, List<String>> partitioned =
            certs.stream().collect(Collectors.partitioningBy(SignerInformationEntity::isDeleted,
                                                             Collectors.mapping(SignerInformationEntity::getKid,
                                                                                Collectors.toList())));

        return new DeltaListDto(partitioned.get(Boolean.FALSE), partitioned.get(Boolean.TRUE));

    }

    /**
     * Gets the deleted/updated state of the certificates after the given value.
     *
     * @return state of the certificates represented by their kids
     */
    public DeltaListDto getDeltaList(ZonedDateTime ifModifiedDateTime) {

        List<SignerInformationEntity> certs =
            signerInformationRepository.findAllByUpdatedAtAfterOrderByIdAsc(ifModifiedDateTime);

        Map<Boolean, List<String>> partitioned =
            certs.stream().collect(Collectors.partitioningBy(SignerInformationEntity::isDeleted,
                                                             Collectors.mapping(SignerInformationEntity::getKid,
                                                                                Collectors.toList())));

        return new DeltaListDto(partitioned.get(Boolean.FALSE), partitioned.get(Boolean.TRUE));

    }

    /**
     * Gets the raw data of the certificates for a given kid list.
     *
     * @param requestedCertList list of kids
     * @return raw data of certificates
     */
    public Map<String, List<CertificatesLookupResponseItemDto>> getCertificatesData(List<String> requestedCertList) {

        List<SignerInformationEntity> certs =
            signerInformationRepository.findAllByKidIn(requestedCertList);

        return certs.stream().collect(Collectors.groupingBy(SignerInformationEntity::getCountry,
                                                            Collectors.mapping(this::map, Collectors.toList())));
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

    /**
     * Returns a list of all active certificates.
     *
     * @return List of SignerInformationEntity
     */
    public List<SignerInformationEntity> getActiveCertificates() {
        return signerInformationRepository.getAllByDeletedIs(false);
    }

    /**
     * Returns a list of active certificates for given list of countries.
     *
     * @param countries List of Country Codes to filter for.     *
     * @return List of SignerInformationEntity
     */
    public List<SignerInformationEntity> getActiveCertificatesForCountries(List<String> countries) {
        return signerInformationRepository.getAllByDeletedIsAndCountryIsIn(false, countries);
    }

    private CertificatesLookupResponseItemDto map(SignerInformationEntity entity) {

        return new CertificatesLookupResponseItemDto(entity.getKid(), entity.getRawData());
    }


    public List<SignerInformationEntity> getActiveCertificatesForFilter(String domain, String participant){
        if (domain != null && participant != null){
            return signerInformationRepository.getAllByDeletedIsAndDomainIsAndCountryIs(false, domain, participant);
        }else if (domain != null){
            return signerInformationRepository.getAllByDeletedIsAndDomainIs(false, domain);
        }else{
            return getActiveCertificates();
        }
    }

    public List<String> getDomainsList() {
        return signerInformationRepository.getDomainsList();
    }
}

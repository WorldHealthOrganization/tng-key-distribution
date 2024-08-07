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

package tng.trustnetwork.keydistribution.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tng.trustnetwork.keydistribution.entity.SignerInformationEntity;

public interface SignerInformationRepository extends JpaRepository<SignerInformationEntity, Long> {

    List<SignerInformationEntity> getByCountryIs(String country);

    List<SignerInformationEntity> getByDomainIs(String domain);

    List<SignerInformationEntity> getByDomainIsAndCountryIs(String domain, String country);

    List<SignerInformationEntity> getByCountryIsAndGroupIs(String country, String group);

    List<SignerInformationEntity> getByCountryIsAndGroupIsAndKidIs(String country, String group, String kid);

    List<SignerInformationEntity> getByDomainIsAndGroupIs(String domain, String group);

    List<SignerInformationEntity> getByDomainIsAndGroupIsAndKidIs(String domain, String group, String kid);

    List<SignerInformationEntity> getByGroupIs(String group);

    List<SignerInformationEntity> getByGroupIsAndKidIs(String group, String kid);

    List<SignerInformationEntity> getByDomainIsAndCountryIsAndGroupIs(String domain, String country, String group);

    List<SignerInformationEntity> getByDomainIsAndCountryIsAndGroupIsAndKidIs(
        String domain, String country, String group, String kid);

    List<SignerInformationEntity> getBySubjectHashIsAndCountryIsAndDomainIs(
        String subjectHash, String country, String domain);

    @Query("SELECT DISTINCT s.country FROM SignerInformationEntity s"
        + " UNION SELECT DISTINCT t.country FROM TrustedIssuerEntity t")
    List<String> getCountryList();

    @Query("SELECT DISTINCT s.domain FROM SignerInformationEntity s"
        + " UNION SELECT DISTINCT t.domain FROM TrustedIssuerEntity t")
    List<String> getDomainsList();

    @Query("SELECT DISTINCT s.group FROM SignerInformationEntity s")
    List<String> getGroupList();
}

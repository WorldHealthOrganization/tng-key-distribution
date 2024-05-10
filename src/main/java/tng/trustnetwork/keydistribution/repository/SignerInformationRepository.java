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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tng.trustnetwork.keydistribution.entity.SignerInformationEntity;

public interface SignerInformationRepository extends JpaRepository<SignerInformationEntity, Long> {

    Optional<SignerInformationEntity> findFirstByIdIsNotNullOrderByIdAsc();

    Optional<SignerInformationEntity> findFirstByIdGreaterThanOrderByIdAsc(Long id);

    List<SignerInformationEntity> findAllByOrderByIdAsc();

    @Modifying
    @Query("UPDATE SignerInformationEntity s SET s.deleted = true WHERE s.kid not in :kids")
    void setDeletedByKidsNotIn(@Param("kids") List<String> kids);

    @Modifying
    @Query("UPDATE SignerInformationEntity s SET s.deleted = true")
    void setAllDeleted();

    void deleteByKidNotIn(List<String> kids);


    List<SignerInformationEntity> findAllByDeletedOrderByIdAsc(boolean deleted);

    void deleteByKidIn(List<String> kids);

    List<SignerInformationEntity> findAllByUpdatedAtAfterOrderByIdAsc(ZonedDateTime ifModifiedDateTime);

    List<SignerInformationEntity> findAllByKidIn(List<String> kids);

    Optional<SignerInformationEntity> findFirstByIdIsNotNullAndDeletedOrderByIdAsc(boolean deleted);

    Optional<SignerInformationEntity> findFirstByIdGreaterThanAndDeletedOrderByIdAsc(Long resumeToken, boolean deleted);

    @Query("SELECT DISTINCT s.country FROM SignerInformationEntity s WHERE s.deleted = false")
    List<String> getCountryList();

    List<SignerInformationEntity> getAllByDeletedIs(boolean deleted);

    List<SignerInformationEntity> getAllByDeletedIsAndCountryIsIn(boolean deleted, List<String> countries);

    @Query("SELECT DISTINCT s.domain FROM SignerInformationEntity s WHERE s.deleted = false")
    List<String> getDomainsList();


    List<SignerInformationEntity> getAllByDeletedIsAndDomainIs(boolean deleted, String domain);

    List<SignerInformationEntity> getAllByDeletedIsAndDomainIsAndCountryIs(boolean deleted, String domain, String country);


}

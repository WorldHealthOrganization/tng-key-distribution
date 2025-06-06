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
import tng.trustnetwork.keydistribution.entity.TrustedIssuerEntity;

public interface TrustedIssuerRepository extends JpaRepository<TrustedIssuerEntity, Long> {

    List<TrustedIssuerEntity> findAllByUrlTypeIs(TrustedIssuerEntity.UrlType urlType);

    List<TrustedIssuerEntity> findAllByUrlTypeIsAndDomainIs(TrustedIssuerEntity.UrlType urlType, String domain);

    List<TrustedIssuerEntity> findAllByUrlTypeIsAndCountryIs(TrustedIssuerEntity.UrlType urlType, String country);

    List<TrustedIssuerEntity> findAllByUrlTypeIsAndDomainIsAndCountryIs(
        TrustedIssuerEntity.UrlType urlType, String domain, String country);
}

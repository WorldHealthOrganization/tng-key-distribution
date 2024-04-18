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

package tng.trustnetwork.keydistribution.mapper;

import eu.europa.ec.dgc.gateway.connector.model.TrustedIssuer;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tng.trustnetwork.keydistribution.dto.TrustedIssuerDto;
import tng.trustnetwork.keydistribution.entity.TrustedIssuerEntity;

@Mapper(componentModel = "spring")
public interface IssuerMapper {

    @Mapping(source = "type", target = "urlType")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "etag", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    TrustedIssuerEntity trustedIssuerToTrustedIssuerEntity(TrustedIssuer trustedIssuer);


    List<TrustedIssuerEntity> trustedIssuerToTrustedIssuerEntity(List<TrustedIssuer> trustedIssuer);

    @Mapping(source = "urlType", target = "type")
    @Mapping(source = "createdAt", target = "timestamp")
    TrustedIssuerDto trustedIssuerEntityToTrustedIssuerDto(TrustedIssuerEntity trustedIssuerEntity);

    @Mapping(source = "createdAt", target = "timestamp")
    List<TrustedIssuerDto> trustedIssuerEntityToTrustedIssuerDto(List<TrustedIssuerEntity> trustedIssuerEntities);

}

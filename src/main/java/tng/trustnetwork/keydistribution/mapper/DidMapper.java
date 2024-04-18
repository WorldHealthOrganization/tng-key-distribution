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

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;
import tng.trustnetwork.keydistribution.entity.DecentralizedIdentifierEntity;
import tng.trustnetwork.keydistribution.entity.EcPublicKeyJwkEntity;
import tng.trustnetwork.keydistribution.entity.PublicKeyJwkEntity;
import tng.trustnetwork.keydistribution.entity.RsaPublicKeyJwkEntity;
import tng.trustnetwork.keydistribution.entity.VerificationMethodEntity;
import tng.trustnetwork.keydistribution.model.DidDocument;
import tng.trustnetwork.keydistribution.model.EcPublicKeyJwk;
import tng.trustnetwork.keydistribution.model.JwkVerificationMethod;
import tng.trustnetwork.keydistribution.model.PublicKeyJwk;
import tng.trustnetwork.keydistribution.model.RsaPublicKeyJwk;
import tng.trustnetwork.keydistribution.model.StringOrObject;
import tng.trustnetwork.keydistribution.model.VerificationMethod;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public interface DidMapper {

    @Mapping(target = "didId", source = "didDocument.id")
    @Mapping(target = "verificationMethods", source = "didDocument.verificationMethod")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    DecentralizedIdentifierEntity toEntity(DidDocument didDocument, String raw);

    @SubclassMapping(target = RsaPublicKeyJwkEntity.class, source = RsaPublicKeyJwk.class)
    @SubclassMapping(target = EcPublicKeyJwkEntity.class, source = EcPublicKeyJwk.class)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    PublicKeyJwkEntity toEntity(PublicKeyJwk publicKeyJwk);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    EcPublicKeyJwkEntity toEntity(EcPublicKeyJwk model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    RsaPublicKeyJwkEntity toEntity(RsaPublicKeyJwk model);

    @SubclassMapping(target = VerificationMethodEntity.class, source = JwkVerificationMethod.class)
    @Mapping(target = "vmId", source = "verificationMethod.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "parentDocument", ignore = true)
    @Mapping(target = "publicKeyJwk", ignore = true)
    VerificationMethodEntity toEntity(VerificationMethod verificationMethod);

    @Mapping(target = "type", constant = "JsonWebKey2020")
    @Mapping(target = "vmId", source = "verificationMethod.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentDocument", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    VerificationMethodEntity toEntity(JwkVerificationMethod verificationMethod);

    default <T> T unwrap(StringOrObject<T> wrapped) {
        return wrapped.getObjectValue();
    }

    default String toSingleString(List<String> list) {

        return list == null ? null : String.join(",", list);
    }
}

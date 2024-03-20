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
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "verificationMethods", source = "didDocument.verificationMethod")
    DecentralizedIdentifierEntity toEntity(DidDocument didDocument, String raw);

    @SubclassMapping(target = RsaPublicKeyJwkEntity.class, source = RsaPublicKeyJwk.class)
    @SubclassMapping(target = EcPublicKeyJwkEntity.class, source = EcPublicKeyJwk.class)
    PublicKeyJwkEntity toEntity(PublicKeyJwk publicKeyJwk);

    @Mapping(target = "xvalue", source = "x")
    @Mapping(target = "yvalue", source = "y")
    EcPublicKeyJwkEntity toEntity(EcPublicKeyJwk model);

    @Mapping(target = "evalue", source = "e")
    @Mapping(target = "nvalue", source = "n")
    RsaPublicKeyJwkEntity toEntity(RsaPublicKeyJwk model);

    @SubclassMapping(target = VerificationMethodEntity.class, source = JwkVerificationMethod.class)
    VerificationMethodEntity toEntity(VerificationMethod verificationMethod);

    @Mapping(target = "type", constant = "JsonWebKey2020")
    @Mapping(target = "vmId", source = "verificationMethod.id")
    @Mapping(target = "id", ignore = true)
    VerificationMethodEntity toEntity(JwkVerificationMethod verificationMethod);

    default <T> T unwrap(StringOrObject<T> wrapped) {
        return wrapped.getObjectValue();
    }

    default String toSingleString(List<String> list) {

        return String.join(",", list);
    }
}

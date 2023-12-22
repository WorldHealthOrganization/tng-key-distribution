package tng.trustnetwork.keydistribution.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import tng.trustnetwork.keydistribution.entity.ProofEntity;
import tng.trustnetwork.keydistribution.entity.VerificationMethodEntity;
import tng.trustnetwork.keydistribution.model.Proof;
import tng.trustnetwork.keydistribution.model.ResolvedKey;
import tng.trustnetwork.keydistribution.model.VerificationMethod;

@Mapper(componentModel = "spring")
public interface DecentralizedIdentifierMapper {
	
	ProofEntity proofToProofEntity(Proof proof);
	
	@Mapping(source = "publicKeyJwk.x5c", target = "publicKeyJwk.x5c", qualifiedByName = "x5cMapping")
	@Mapping(source = "id", target = "vmId")
	@Mapping(target = "id", ignore = true)
	VerificationMethodEntity resolvedKeyToVerificationMethodEntity(ResolvedKey resolvedKey);
	
	
	@Named("x5cMapping")
    default String x5cMapping(List<String> x5c) {
        return String.join(",", x5c);
    }

}

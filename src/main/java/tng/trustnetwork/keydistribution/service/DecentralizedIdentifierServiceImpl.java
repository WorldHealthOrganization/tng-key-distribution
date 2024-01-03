package tng.trustnetwork.keydistribution.service;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import tng.trustnetwork.keydistribution.entity.DecentralizedIdentifierEntity;
import tng.trustnetwork.keydistribution.entity.ProofEntity;
import tng.trustnetwork.keydistribution.entity.TrustedIssuerEntity;
import tng.trustnetwork.keydistribution.entity.VerificationMethodEntity;
import tng.trustnetwork.keydistribution.mapper.DecentralizedIdentifierMapper;
import tng.trustnetwork.keydistribution.mapper.IssuerMapper;
import tng.trustnetwork.keydistribution.model.DidDocumentUnmarshal;
import tng.trustnetwork.keydistribution.model.Proof;
import tng.trustnetwork.keydistribution.model.ResolvedKey;
import tng.trustnetwork.keydistribution.repository.DecentralizedIdentifierRepository;

@Slf4j
@Service
public class DecentralizedIdentifierServiceImpl implements DecentralizedIdentifierService {

    @Autowired
    DecentralizedIdentifierRepository decentralizedIdentifierRepository;

    @Autowired
    DecentralizedIdentifierMapper decentralizedIdentifierMapper;

    @Override
    public List<DecentralizedIdentifierEntity> getDecentralizedIdentifierEntity(String didKey) {
        // TODO Auto-generated method stub
        return decentralizedIdentifierRepository.findByDidId(didKey);
    }

    @Override
    public void updateDecentralizedIdentifierList(DidDocumentUnmarshal didDocumentUnmarshal) {
        // TODO Auto-generated method stub
        if (null != didDocumentUnmarshal) {
            DecentralizedIdentifierEntity decentralizedIdentifierEntity = new DecentralizedIdentifierEntity();
            decentralizedIdentifierEntity.setDidId(didDocumentUnmarshal.getId());
            decentralizedIdentifierEntity.setController(didDocumentUnmarshal.getController());
            decentralizedIdentifierEntity.setContext(String.join(",", didDocumentUnmarshal.getContext()));
            decentralizedIdentifierEntity.setProof(
                decentralizedIdentifierMapper.proofToProofEntity(didDocumentUnmarshal.getProof()));

            List<VerificationMethodEntity> verificationMethodEntities = new ArrayList<>();

            List<ResolvedKey> resolvedKeys = didDocumentUnmarshal.getVerificationMethod().getResolvedKeys();

            for (ResolvedKey resolvedKey : resolvedKeys) {
                verificationMethodEntities.add(
                    decentralizedIdentifierMapper.resolvedKeyToVerificationMethodEntity(resolvedKey));
            }

            decentralizedIdentifierEntity.setVerificationMethod(verificationMethodEntities);
            decentralizedIdentifierRepository.save(decentralizedIdentifierEntity);

        }
    }

}

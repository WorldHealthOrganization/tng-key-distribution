package tng.trustnetwork.keydistribution.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tng.trustnetwork.keydistribution.entity.DecentralizedIdentifierEntity;
import tng.trustnetwork.keydistribution.entity.VerificationMethodEntity;
import tng.trustnetwork.keydistribution.model.DidDocument;
import tng.trustnetwork.keydistribution.repository.DecentralizedIdentifierRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class DecentralizedIdentifierService {

    private final DecentralizedIdentifierRepository decentralizedIdentifierRepository;

    public void updateDecentralizedIdentifierList(DidDocument didDocumentUnmarshal, String raw) {
        // TODO Auto-generated method stub
        if (null != didDocumentUnmarshal) {
            DecentralizedIdentifierEntity decentralizedIdentifierEntity = new DecentralizedIdentifierEntity();
            decentralizedIdentifierEntity.setDidId(didDocumentUnmarshal.getId());

            List<VerificationMethodEntity> verificationMethodEntities = new ArrayList<>();

            /*List<ResolvedKey> resolvedKeys = didDocumentUnmarshal.getVerificationMethod().getResolvedKeys();

            for (ResolvedKey resolvedKey : resolvedKeys) {
                verificationMethodEntities.add(
                    decentralizedIdentifierMapper.resolvedKeyToVerificationMethodEntity(resolvedKey));
            }

            decentralizedIdentifierEntity.setVerificationMethod(verificationMethodEntities);*/
            decentralizedIdentifierRepository.save(decentralizedIdentifierEntity);

        }
    }

}

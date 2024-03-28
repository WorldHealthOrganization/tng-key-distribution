package tng.trustnetwork.keydistribution.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tng.trustnetwork.keydistribution.entity.DecentralizedIdentifierEntity;
import tng.trustnetwork.keydistribution.mapper.DidMapper;
import tng.trustnetwork.keydistribution.model.DidDocument;
import tng.trustnetwork.keydistribution.repository.DecentralizedIdentifierRepository;
import tng.trustnetwork.keydistribution.repository.PublicKeyJwkRepository;
import tng.trustnetwork.keydistribution.repository.VerificationMethodRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class DecentralizedIdentifierService {

    private final DecentralizedIdentifierRepository decentralizedIdentifierRepository;

    private final VerificationMethodRepository verificationMethodRepository;

    private final PublicKeyJwkRepository publicKeyJwkRepository;

    private final DidMapper didMapper;

    /**
     * Update list of stored DID with given Document.
     *
     * @param didDocument Parsed DIDDocument
     * @param raw         RAW-JSON-Value of the DID Document. This will be stored to allow validation of LD-Proof later.
     */
    @Transactional
    public void updateDecentralizedIdentifierList(DidDocument didDocument, String raw) {

        DecentralizedIdentifierEntity didEntity = didMapper.toEntity(didDocument, raw);
        decentralizedIdentifierRepository.save(didEntity);

        didEntity.getVerificationMethods()
                 .stream()
                 .filter(Objects::nonNull)
                 .forEach(verificationMethod -> {

                     verificationMethod.setParentDocument(didEntity);
                     publicKeyJwkRepository.save(verificationMethod.getPublicKeyJwk());
                     verificationMethodRepository.save(verificationMethod);
                 });
    }

}

package tng.trustnetwork.keydistribution.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tng.trustnetwork.keydistribution.model.DidDocument;
import tng.trustnetwork.keydistribution.model.DidDocumentUnmarshal;
import tng.trustnetwork.keydistribution.model.VerificationMethod;


@Slf4j
@Service
public class UniversalResolverServiceImpl implements UniversalResolverService {

    @Autowired
    private UniversalResolverClient universalResolverClient;

    @Override
    public DidDocumentUnmarshal universalResolverApiCall(String didKey) {

        DidDocument did = universalResolverClient.getDidDocument(didKey);

        if (null != did) {
            ObjectMapper mapper = new ObjectMapper();

            try {

                String verificationMethodStr = mapper.writeValueAsString(did.getVerificationMethod());
                VerificationMethod verificationMethod =
                    mapper.readValue(verificationMethodStr, VerificationMethod.class);

                DidDocumentUnmarshal didDocumentUnmarshal = new DidDocumentUnmarshal();
                didDocumentUnmarshal.setContext(did.getContext());
                didDocumentUnmarshal.setController(did.getController());
                didDocumentUnmarshal.setId(did.getId());
                didDocumentUnmarshal.setProof(did.getProof());
                didDocumentUnmarshal.setVerificationMethod(verificationMethod);
                return didDocumentUnmarshal;

            } catch (JsonProcessingException ex) {
                log.error("Issue parsing DID document", ex);
            }
        }

        return null;
    }

}

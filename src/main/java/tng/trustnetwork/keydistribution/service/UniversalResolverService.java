package tng.trustnetwork.keydistribution.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tng.trustnetwork.keydistribution.clients.UniversalResolverClient;
import tng.trustnetwork.keydistribution.model.DidDocument;


@Slf4j
@Service
@RequiredArgsConstructor
public class UniversalResolverService {

    private final UniversalResolverClient universalResolverClient;

    private final ObjectMapper objectMapper;

    /**
     * Try to resolve DID Document by ID at UniversalResolverService.
     *
     * @param didId Identifier of document to resolve
     * @return Parsed and RAW DID Document
     * @throws JsonProcessingException when parsing of downloaded document failed.
     */
    public DidDocumentWithRawResponse universalResolverApiCall(String didId) throws JsonProcessingException {

        String rawResponse = universalResolverClient.getDidDocument(didId);
        DidDocument didDocument = objectMapper.readValue(rawResponse, DidDocument.class);

        return new DidDocumentWithRawResponse(didDocument, rawResponse);
    }

    public record DidDocumentWithRawResponse(
        DidDocument didDocument,

        String raw) {
    }

}

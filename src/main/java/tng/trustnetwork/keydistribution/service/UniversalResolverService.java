package tng.trustnetwork.keydistribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tng.trustnetwork.keydistribution.clients.UniversalResolverClient;
import tng.trustnetwork.keydistribution.model.DidDocument;


@Slf4j
@Service
@RequiredArgsConstructor
public class UniversalResolverService {

    private final UniversalResolverClient universalResolverClient;

    public DidDocumentWithRawResponse universalResolverApiCall(String didKey) {

        ResponseEntity<DidDocument> response = universalResolverClient.getDidDocument(didKey);

        return new DidDocumentWithRawResponse(response.getBody(), response.toString());
    }

    public record DidDocumentWithRawResponse(
        DidDocument didDocument,

        String raw) {
    }

}

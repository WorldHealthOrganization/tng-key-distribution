package tng.trustnetwork.keydistribution.restapi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import tng.trustnetwork.keydistribution.entity.DecentralizedIdentifierEntity;
import tng.trustnetwork.keydistribution.model.DidDocumentUnmarshal;
import tng.trustnetwork.keydistribution.service.DecentralizedIdentifierService;
import tng.trustnetwork.keydistribution.service.UniversalResolverService;

@RestController
public class UniversalResolverController {

    @Autowired
    private UniversalResolverService urService;

    @Autowired
    private DecentralizedIdentifierService decentralizedIdentifierService;

    /**
     * Fetches DID document.
     *
     * @param didKey key id of the did document
     * @return ResponseEntity as http response
     */
    @GetMapping(path = "/getDID/{didKey}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Tag(name = "Temporary Testing API")
    public ResponseEntity<DidDocumentUnmarshal> getDidDocument(@PathVariable String didKey) {

        DidDocumentUnmarshal didDocumentUnmarshal = urService.universalResolverApiCall(didKey);
        return ResponseEntity.ok(didDocumentUnmarshal);

    }

    /**
     * Fetches DID document from Database.
     *
     * @param didKey key id of the did document
     * @return ResponseEntity as http response
     */
    @GetMapping(path = "/getDIDFromDatabase/{didKey}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Tag(name = "Temporary Testing API")
    public ResponseEntity<List<DecentralizedIdentifierEntity>> getDidDocumentFromDatabase(@PathVariable String didKey) {

        List<DecentralizedIdentifierEntity> decentralizedIdentifierEntity =
            decentralizedIdentifierService.getDecentralizedIdentifierEntity(didKey);
        return ResponseEntity.ok(decentralizedIdentifierEntity);

    }

}

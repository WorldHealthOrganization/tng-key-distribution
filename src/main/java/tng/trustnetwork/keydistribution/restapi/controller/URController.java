package tng.trustnetwork.keydistribution.restapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import tng.trustnetwork.keydistribution.model.DIDDocumentUnmarshal;
import tng.trustnetwork.keydistribution.service.URService;

@RestController
public class URController {
	
	@Autowired
	private URService urService;
	
	@GetMapping(path = "/getDID/{didKey}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DIDDocumentUnmarshal> getDIDDocument(@PathVariable String didKey){
		
		DIDDocumentUnmarshal DIDDocumentUnmarshal = urService.URApiCall(didKey);
		return ResponseEntity.ok(DIDDocumentUnmarshal);
		
		
	}

}

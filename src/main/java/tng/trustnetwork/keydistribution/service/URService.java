package tng.trustnetwork.keydistribution.service;

import tng.trustnetwork.keydistribution.model.DIDDocumentUnmarshal;

public interface URService {
	
	public  DIDDocumentUnmarshal URApiCall(String didKey);

}

package tng.trustnetwork.keydistribution.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tng.trustnetwork.keydistribution.model.DIDDocument;
import tng.trustnetwork.keydistribution.model.DIDDocumentUnmarshal;
import tng.trustnetwork.keydistribution.model.VerificationMethod;


@Service
public class URServiceImpl implements URService{
	
	@Autowired
	private UniversalResolverClient universalResolverClient;
	
	@Override
	public DIDDocumentUnmarshal URApiCall(String didKey) {
		
		DIDDocument did = universalResolverClient.getDIDDocument(didKey);
		
		if(null != did) {
			  ObjectMapper mapper = new ObjectMapper();
				 
			  try {
				
				String verificationMethodStr = mapper.writeValueAsString(did.getVerificationMethod());
				VerificationMethod  verificationMethod =  mapper.readValue(verificationMethodStr, VerificationMethod.class);
				
				DIDDocumentUnmarshal DIDDocumentUnmarshal  = new DIDDocumentUnmarshal();
				DIDDocumentUnmarshal.setContext(did.getContext());
				DIDDocumentUnmarshal.setController(did.getController());
				DIDDocumentUnmarshal.setId(did.getId());
				DIDDocumentUnmarshal.setProof(did.getProof());
				DIDDocumentUnmarshal.setVerificationMethod(verificationMethod);
				return DIDDocumentUnmarshal;
				 
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

}

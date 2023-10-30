package tng.trustnetwork.keydistribution.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tng.trustnetwork.keydistribution.model.DIDDocument;
import tng.trustnetwork.keydistribution.model.DIDDocumentUnmarshal;
import tng.trustnetwork.keydistribution.model.VerificationMethod;


@Service
public class URServiceImpl implements URService{
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value( "${universal.resolver}" )
	private String universalResolver;

	@Override
	public DIDDocumentUnmarshal URApiCall(String didKey) {
		
		DIDDocument did = restTemplate.getForObject(universalResolver+didKey, DIDDocument.class);
		
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

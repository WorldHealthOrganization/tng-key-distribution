package tng.trustnetwork.keydistribution.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import tng.trustnetwork.keydistribution.model.DIDDocument;
import tng.trustnetwork.keydistribution.model.DIDDocumentUnmarshal;

@SpringBootTest
public class UniversalResolverServiceImplTest {
	
	@MockBean
	private UniversalResolverClient universalResolverClient;
	
	@Autowired
	private URServiceImpl uRServiceImpl;

	
	@Test
	void resolveDIDDocumentNotNull() {
		
		DIDDocument DIDDocument  =  new DIDDocument();
		Mockito.when(universalResolverClient.getDIDDocument("did:web:tng-cdn-dev.who.int:trustlist")).thenReturn(DIDDocument);
		DIDDocumentUnmarshal DIDDocumentUnmarshal = uRServiceImpl.URApiCall("did:web:tng-cdn-dev.who.int:trustlist");
		Assertions.assertNotNull(DIDDocumentUnmarshal);
		
	}
	
	@Test
	void resolveDIDDocumentNull() {
		
		DIDDocument DIDDocument  =  new DIDDocument();
		Mockito.when(universalResolverClient.getDIDDocument("test")).thenReturn(null);
		DIDDocumentUnmarshal DIDDocumentUnmarshal = uRServiceImpl.URApiCall("test");
		Assertions.assertNull(DIDDocumentUnmarshal);
		
	}
	
}

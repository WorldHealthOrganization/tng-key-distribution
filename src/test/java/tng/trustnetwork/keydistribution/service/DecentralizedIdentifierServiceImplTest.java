package tng.trustnetwork.keydistribution.service;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import tng.trustnetwork.keydistribution.entity.DecentralizedIdentifierEntity;
import tng.trustnetwork.keydistribution.mapper.DecentralizedIdentifierMapper;
import tng.trustnetwork.keydistribution.model.DidDocumentUnmarshal;
import tng.trustnetwork.keydistribution.repository.DecentralizedIdentifierRepository;
import tng.trustnetwork.keydistribution.testdata.DecentralizedIdentifierTestHelper;

@SpringBootTest
public class DecentralizedIdentifierServiceImplTest {
	
	
	@Autowired
	private DecentralizedIdentifierService decentralizedIdentifierService;
	
	@MockBean
	private DecentralizedIdentifierMapper decentralizedIdentifierMapper;
	
	@Autowired
	DecentralizedIdentifierTestHelper decentralizedIdentifierTestHelper;
	
	@Autowired
	DecentralizedIdentifierRepository decentralizedIdentifierRepository;
	
	@Test
	void updateDecentralizedIdentifierListTest() {
		
		DidDocumentUnmarshal didDocumentUnmarshal = decentralizedIdentifierTestHelper.creaDidDocumentUnmarshal();
		Mockito.when(decentralizedIdentifierMapper.proofToProofEntity(didDocumentUnmarshal.getProof())).thenReturn(decentralizedIdentifierTestHelper.creaProofEntity());
		
		Mockito.when(decentralizedIdentifierMapper.resolvedKeyToVerificationMethodEntity(didDocumentUnmarshal.getVerificationMethod().getResolvedKeys().get(0))).thenReturn(decentralizedIdentifierTestHelper.createVerificationMethodEntity());
		
		decentralizedIdentifierService.updateDecentralizedIdentifierList(didDocumentUnmarshal);
		
		List<DecentralizedIdentifierEntity> decentralizedIdentifierEntities = decentralizedIdentifierRepository.findAll();
		System.out.println("decentralizedIdentifierEntities.size() : "+ decentralizedIdentifierEntities.size());
	    Assertions.assertEquals(1, decentralizedIdentifierEntities.size());
		
	}

}

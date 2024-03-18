package tng.trustnetwork.keydistribution.service;

import eu.europa.ec.dgc.gateway.connector.DgcGatewayDownloadConnector;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tng.trustnetwork.keydistribution.entity.DecentralizedIdentifierEntity;
import tng.trustnetwork.keydistribution.repository.DecentralizedIdentifierRepository;

@SpringBootTest
public class DecentralizedIdentifierServiceTest {

	 @MockBean
	 DgcGatewayDownloadConnector dgcGatewayDownloadConnector;
	
	@Autowired
	DecentralizedIdentifierRepository decentralizedIdentifierRepository;
	
	@Test
	void updateDecentralizedIdentifierListTest() {
		
		List<DecentralizedIdentifierEntity> decentralizedIdentifierEntities = decentralizedIdentifierRepository.findAll();
		System.out.println("decentralizedIdentifierEntities.size() : "+ decentralizedIdentifierEntities.size());
	    Assertions.assertEquals(1, decentralizedIdentifierEntities.size());
		
	}

}

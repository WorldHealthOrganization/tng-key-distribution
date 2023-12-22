package tng.trustnetwork.keydistribution.service;

import java.util.List;
import tng.trustnetwork.keydistribution.entity.DecentralizedIdentifierEntity;
import tng.trustnetwork.keydistribution.model.DidDocumentUnmarshal;

public interface DecentralizedIdentifierService {
	
	public List<DecentralizedIdentifierEntity> getDecentralizedIdentifierEntity(String didKey);
	public void updateDecentralizedIdentifierList(DidDocumentUnmarshal didDocumentUnmarshal);

}

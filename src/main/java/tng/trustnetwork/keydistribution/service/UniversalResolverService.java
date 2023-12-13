package tng.trustnetwork.keydistribution.service;

import tng.trustnetwork.keydistribution.model.DidDocumentUnmarshal;

public interface UniversalResolverService {

    public DidDocumentUnmarshal universalResolverApiCall(String didKey);

}

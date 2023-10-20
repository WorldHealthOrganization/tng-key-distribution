package tng.trustnetwork.keydistribution.service;

import java.util.List;
import tng.trustnetwork.keydistribution.entity.TrustedIssuerEntity;

public interface TrustListService {

    public String createTrustList(TrustedIssuerEntity trustedIssuerEntity);
    
    public String updateTrustList(TrustedIssuerEntity trustedIssuerEntity);
    
    public String deleteTrustList(Long id);
    
    public TrustedIssuerEntity getTrustList(Long id);
    
    public List<TrustedIssuerEntity> getAllTrustLists();
}

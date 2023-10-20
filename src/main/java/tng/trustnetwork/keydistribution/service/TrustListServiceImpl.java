package tng.trustnetwork.keydistribution.service;

import java.util.List;
import org.springframework.stereotype.Service;
import tng.trustnetwork.keydistribution.entity.TrustedIssuerEntity;
import tng.trustnetwork.keydistribution.exception.TrustListNotFoundException;
import tng.trustnetwork.keydistribution.repository.TrustListRepository;

@Service
public class TrustListServiceImpl implements TrustListService {

    TrustListRepository trustListRepository;

    public TrustListServiceImpl(TrustListRepository trustListRepository) {
        this.trustListRepository = trustListRepository;
    }

    @Override
    public String createTrustList(TrustedIssuerEntity trustedIssuerEntity) {
        trustListRepository.save(trustedIssuerEntity);
        return "Success";
    }

    @Override
    public String updateTrustList(TrustedIssuerEntity trustedIssuerEntity) {
        trustListRepository.save(trustedIssuerEntity);
        return "Success";
    }
    
    @Override
    public String deleteTrustList(Long id) {
        trustListRepository.deleteById(id);
        return "Success";
    }

    @Override
    public TrustedIssuerEntity getTrustList(Long id) {
        
        if (trustListRepository.findById(id).isEmpty()) {
            throw new TrustListNotFoundException("Requested data does not exist");
        } else {
            return trustListRepository.findById(id).get();
        }
    }
    
    @Override
    public List<TrustedIssuerEntity> getAllTrustLists() {
        return trustListRepository.findAll();
    }
}

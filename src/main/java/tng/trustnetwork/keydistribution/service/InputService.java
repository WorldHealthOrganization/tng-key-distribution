package tng.trustnetwork.keydistribution.service;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import tng.trustnetwork.keydistribution.dto.CertificateTypeDto;
import tng.trustnetwork.keydistribution.dto.TrustListItemDto;

public interface InputService {

    public ResponseEntity<String> getDataLoader();
    //    
    //    public List<TrustListItemDto> dataLoader(@PathVariable("type") CertificateTypeDto type);
    //
}

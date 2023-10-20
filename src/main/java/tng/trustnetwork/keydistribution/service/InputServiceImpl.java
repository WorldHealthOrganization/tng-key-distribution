package tng.trustnetwork.keydistribution.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

//@RestController
//@RequestMapping("/RestTemplate")
@Service
public class InputServiceImpl implements InputService {
    @Autowired
    private JwkServiceImpl jwkServiceImpl;

    //@GetMapping(value = "/getAllTrustList")
    public ResponseEntity<String> getDataLoader() {
        return jwkServiceImpl.gatewayApiCall();
    }
}

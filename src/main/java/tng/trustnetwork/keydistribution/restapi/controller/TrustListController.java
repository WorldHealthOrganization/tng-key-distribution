package tng.trustnetwork.keydistribution.restapi.controller;

import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tng.trustnetwork.keydistribution.entity.TrustedIssuerEntity;
import tng.trustnetwork.keydistribution.exception.TrustListNotFoundException;
import tng.trustnetwork.keydistribution.repository.TrustListRepository;
import tng.trustnetwork.keydistribution.response.ResponseHandler;
import tng.trustnetwork.keydistribution.service.TrustListService;

@RestController
@RequestMapping("/trustlist")
public class TrustListController {

    @Autowired
    TrustListService trustListService;
    
    @Autowired
    private TrustListRepository trustListRepository;

    public TrustListController(TrustListService trustListService) {
        this.trustListService = trustListService;
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Trust List id", response = ResponseEntity.class)
    public ResponseEntity<Object> getTrustListById(@PathVariable(value = "id") Long id)
          throws TrustListNotFoundException {
        TrustedIssuerEntity trustedIssuerEntity = trustListRepository.findById(id)
              .orElseThrow(() -> new TrustListNotFoundException("Data not found for this id :: " + id));
        return ResponseHandler.responseBuilder("Requested Details are given here",HttpStatus.OK, 
            trustListService.getTrustList(id));
    }

    @GetMapping
    public List<TrustedIssuerEntity> getAllTrustListDetails() {
        return trustListService.getAllTrustLists();
    }
    
    @PostMapping
    public String createTrustListDetails(@RequestBody TrustedIssuerEntity trustedIssuerEntity) {
        trustListService.createTrustList(trustedIssuerEntity);
        return "Trust List Created Successfully";
    }

    @PutMapping
    public String updateTrustListDetails(@RequestBody TrustedIssuerEntity trustedIssuerEntity) {
        trustListService.updateTrustList(trustedIssuerEntity);
        return "Trust List Updated Successfully";
    }

    @DeleteMapping("/{id}")
    public String deleteTrustListDetails(@PathVariable("id") Long id) {
        trustListService.deleteTrustList(id);
        return "Trust List Deleted Successfully";
    }
}

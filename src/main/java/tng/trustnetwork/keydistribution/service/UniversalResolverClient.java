package tng.trustnetwork.keydistribution.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import tng.trustnetwork.keydistribution.model.DidDocument;

@FeignClient(value = "universalresolver", url = "${universal.resolver}")
public interface UniversalResolverClient {

    @GetMapping(value = "/{didKey}", produces = "application/json")
    DidDocument getDidDocument(@PathVariable("didKey") String didKey);

}

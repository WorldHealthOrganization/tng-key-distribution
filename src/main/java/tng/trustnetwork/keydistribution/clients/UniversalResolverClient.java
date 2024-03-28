package tng.trustnetwork.keydistribution.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "universalresolver", url = "${universal.resolver}",
    configuration = UniversalResolverClientConfig.class)
public interface UniversalResolverClient {

    @GetMapping(value = "/{didKey}", produces = "application/json")
    String getDidDocument(@PathVariable("didKey") String didKey);

}

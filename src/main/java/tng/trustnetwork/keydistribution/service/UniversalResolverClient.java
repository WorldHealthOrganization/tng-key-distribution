package tng.trustnetwork.keydistribution.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import tng.trustnetwork.keydistribution.model.DIDDocument;

@FeignClient(value = "universalresolver", url = "${universal.resolver}")
public interface UniversalResolverClient {
	
	 @RequestMapping(method = RequestMethod.GET, value = "/{didKey}", produces = "application/json")
	 DIDDocument  getDIDDocument(@PathVariable("didKey") String didKey);
	
}

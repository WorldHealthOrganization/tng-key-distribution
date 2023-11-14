package tng.trustnetwork.keydistribution.service;

import eu.europa.ec.dgc.gateway.connector.dto.CertificateTypeDto;
import eu.europa.ec.dgc.gateway.connector.dto.TrustListItemDto;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import tng.trustnetwork.keydistribution.exception.BadRequestException;


@RestController
@Slf4j
//@Service
public class InputServiceImpl implements InputService {

    @Autowired
    private JwkService trustListTypeService;


    @GetMapping(path = "/trustList/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TrustListItemDto> dataLoader(@PathVariable("type") CertificateTypeDto type) throws BadRequestException {
        return trustListTypeService.gatewayApiForType(type);
    }

    @GetMapping(path = "/trustList", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TrustListItemDto> dataLoader() throws BadRequestException {
        log.info("Trust list call started");
        return trustListTypeService.gatewayApiForAll();

    }
}

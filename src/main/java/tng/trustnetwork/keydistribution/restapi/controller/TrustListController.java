package tng.trustnetwork.keydistribution.restapi.controller;

import eu.europa.ec.dgc.gateway.connector.dto.CertificateTypeDto;
import eu.europa.ec.dgc.gateway.connector.dto.TrustListItemDto;
import eu.europa.ec.dgc.gateway.connector.dto.TrustedCertificateTrustListDto;
import eu.europa.ec.dgc.gateway.connector.dto.TrustedIssuerDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tng.trustnetwork.keydistribution.exception.BadRequestException;
import tng.trustnetwork.keydistribution.service.TrustListService;


@RestController
@Slf4j
public class TrustListController {
    @Autowired
    private TrustListService trustListTypeService;


    @GetMapping(path = "/trustList/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(tags = {"Trust Lists"})
    public ResponseEntity<List<TrustListItemDto>> dataLoader(@PathVariable("type")
                                                                 CertificateTypeDto type) throws BadRequestException {
        return trustListTypeService.gatewayApiForType(type);
    }

    @GetMapping(path = "trustList/issuers", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Returns the list of trusted issuers filtered by criterias.",
        tags = {"Trust Lists"},
        parameters = {
            @Parameter(
                in = ParameterIn.QUERY,
                name = "country",
                description = "Two-Digit Country Code",
                examples = {@ExampleObject("EU"), @ExampleObject("DE")}
              ),
            @Parameter(
                in = ParameterIn.QUERY,
                name = "domain",
                description = "Value for Domain to search for",
                examples = {@ExampleObject("DCC"), @ExampleObject("ICAO")}
              ),
            @Parameter(
                in = ParameterIn.QUERY,
                name = "withFederation",
                description = "Switch if federated entities should be included",
                allowEmptyValue = true,
                schema = @Schema(implementation = Boolean.class)
              )
        })
        ResponseEntity<List<TrustedIssuerDto>> downloadTrustedIssuers(@RequestParam Map<String, String> queryParams) {

        return trustListTypeService.gatewayApiForIssuers(queryParams);

    }


    @GetMapping(path = "trustList/certificate", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Returns a filtered list of trusted certificates. The provided search criteria are additive."
            + " It is possible to provide more than one value for each criteria. (Except for withFederation)",
        tags = {"Trust Lists"},
        parameters = {
            @Parameter(
                in = ParameterIn.QUERY,
                name = "group",
                description = "Value for Group to search for",
                examples = {@ExampleObject("AUTHENTICATION"), @ExampleObject("AUTHENTICATION_FEDERATION"),
                    @ExampleObject("UPLOAD"), @ExampleObject("CSCA"), @ExampleObject("TRUSTANCHOR"),
                    @ExampleObject("DSC"), @ExampleObject("SIGN"), @ExampleObject("AUTH"), @ExampleObject("CUSTOM")}
              ),
            @Parameter(
                in = ParameterIn.QUERY,
                name = "country",
                description = "Two-Digit Country Code",
                examples = {@ExampleObject("EU"), @ExampleObject("DE")}
              ),
            @Parameter(
                in = ParameterIn.QUERY,
                name = "domain",
                description = "Value for Domain to search for",
                examples = {@ExampleObject("DCC"), @ExampleObject("ICAO")}
              ),
            @Parameter(
                in = ParameterIn.QUERY,
                name = "withFederation",
                description = "Switch if federated entities should be included",
                allowEmptyValue = true,
                schema = @Schema(implementation = Boolean.class)
              )
        })
    public ResponseEntity<List<TrustedCertificateTrustListDto>>
      dataLoaderCertificate(@RequestParam Map<String, String> queryParams) throws BadRequestException {
        return trustListTypeService.gatewayApiForCertificate(queryParams);
    }

}

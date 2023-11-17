package tng.trustnetwork.keydistribution.service;

import eu.europa.ec.dgc.gateway.connector.client.DgcGatewayConnectorRestClient;
import eu.europa.ec.dgc.gateway.connector.dto.CertificateTypeDto;
import eu.europa.ec.dgc.gateway.connector.dto.TrustListItemDto;
import eu.europa.ec.dgc.gateway.connector.dto.TrustedCertificateTrustListDto;
import eu.europa.ec.dgc.gateway.connector.dto.TrustedIssuerDto;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@RequiredArgsConstructor
public class TrustListService {

    @Autowired
   private final DgcGatewayConnectorRestClient dgcGatewayConnectorRestClient;

    public ResponseEntity<List<TrustListItemDto>> gatewayApiForType(CertificateTypeDto type) {
        return ResponseEntity.ok().body(dgcGatewayConnectorRestClient.getTrustList(type).getBody());
    }


    public ResponseEntity<List<TrustedIssuerDto>> gatewayApiForIssuers(Map<String, String> queryParams) {
        return ResponseEntity.ok().body(dgcGatewayConnectorRestClient.downloadTrustedIssuers(queryParams).getBody());
    }

    public ResponseEntity<List<TrustedCertificateTrustListDto>> gatewayApiForCertificate(
        Map<String, String> queryParams) {
        return ResponseEntity.ok().body(
            dgcGatewayConnectorRestClient.downloadTrustedCertificates(queryParams).getBody());
    }


}

package tng.trustnetwork.keydistribution.service;

import eu.europa.ec.dgc.gateway.connector.dto.CertificateTypeDto;
import eu.europa.ec.dgc.gateway.connector.dto.TrustListItemDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tng.trustnetwork.keydistribution.client.KdsGatewayConnectorRestClient;

@Service
@RequiredArgsConstructor
public class JwkService {
    
    private KdsGatewayConnectorRestClient kdsGatewayConnectorRestClient;
    
    public List<TrustListItemDto> gatewayApiForAll() {
        return kdsGatewayConnectorRestClient.getTrustedCertificates();
    }

    public List<TrustListItemDto> gatewayApiForType(CertificateTypeDto type) {
        return kdsGatewayConnectorRestClient.getTrustedCertificates(type);
    }

}

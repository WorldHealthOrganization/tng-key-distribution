package tng.trustnetwork.keydistribution.service;

import eu.europa.ec.dgc.gateway.connector.dto.CertificateTypeDto;
import eu.europa.ec.dgc.gateway.connector.dto.TrustListItemDto;
import java.util.List;

public interface JwkService {
    public List<TrustListItemDto> gatewayApiForAll();

    public List<TrustListItemDto> gatewayApiForType(CertificateTypeDto type);
}

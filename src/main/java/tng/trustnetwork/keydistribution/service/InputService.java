package tng.trustnetwork.keydistribution.service;

import eu.europa.ec.dgc.gateway.connector.dto.CertificateTypeDto;
import eu.europa.ec.dgc.gateway.connector.dto.TrustListItemDto;
import java.util.List;

public interface InputService {
    public List<TrustListItemDto> dataLoader(CertificateTypeDto type);

    public List<TrustListItemDto> dataLoader();
}

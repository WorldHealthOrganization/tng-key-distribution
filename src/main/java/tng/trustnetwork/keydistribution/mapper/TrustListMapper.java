package tng.trustnetwork.keydistribution.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import tng.trustnetwork.keydistribution.dto.TrustedIssuerDto;
import tng.trustnetwork.keydistribution.model.TrustedIssuer;


@Mapper(componentModel = "spring")
public interface TrustListMapper {

    TrustedIssuer map(TrustedIssuerDto trustedIssuerDto);

    List<TrustedIssuer> map(List<TrustedIssuerDto> trustedIssuerDto);
}
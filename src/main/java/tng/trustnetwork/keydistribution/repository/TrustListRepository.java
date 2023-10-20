package tng.trustnetwork.keydistribution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tng.trustnetwork.keydistribution.entity.TrustedIssuerEntity;

public interface TrustListRepository extends JpaRepository<TrustedIssuerEntity, Long> {
}

package tng.trustnetwork.keydistribution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tng.trustnetwork.keydistribution.entity.VerificationMethodEntity;

public interface VerificationMethodRepository extends JpaRepository<VerificationMethodEntity, Long> {
}

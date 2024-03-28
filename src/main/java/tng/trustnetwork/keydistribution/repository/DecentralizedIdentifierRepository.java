package tng.trustnetwork.keydistribution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tng.trustnetwork.keydistribution.entity.DecentralizedIdentifierEntity;

public interface DecentralizedIdentifierRepository extends JpaRepository<DecentralizedIdentifierEntity, Long> {
}

package tng.trustnetwork.keydistribution.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tng.trustnetwork.keydistribution.entity.DecentralizedIdentifierEntity;

public interface DecentralizedIdentifierRepository extends JpaRepository<DecentralizedIdentifierEntity, String> {
    List<DecentralizedIdentifierEntity> findAllById(String id);

    List<DecentralizedIdentifierEntity> findByDidId(String id);
}

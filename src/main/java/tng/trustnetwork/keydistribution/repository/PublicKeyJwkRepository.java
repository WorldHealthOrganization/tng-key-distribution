package tng.trustnetwork.keydistribution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tng.trustnetwork.keydistribution.entity.PublicKeyJwkEntity;

public interface PublicKeyJwkRepository extends JpaRepository<PublicKeyJwkEntity, Long> {
}

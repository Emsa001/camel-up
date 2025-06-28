package tuc.isse.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tuc.isse.entities.CamelEntity;

public interface CamelRepository extends JpaRepository<CamelEntity, Long> {
}

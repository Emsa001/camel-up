package tuc.isse.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tuc.isse.entities.PlayerEntity;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {
     Optional<PlayerEntity> findByName(String username);
}

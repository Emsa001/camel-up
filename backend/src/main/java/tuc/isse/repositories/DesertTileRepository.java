package tuc.isse.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tuc.isse.entities.DesertTileEntity;
import tuc.isse.entities.GameEntity;
import tuc.isse.entities.PlayerEntity;

import java.util.List;
import java.util.Optional;

public interface DesertTileRepository extends JpaRepository<DesertTileEntity, Long> {
    Optional<DesertTileEntity> findByOwner(PlayerEntity owner);
    List<DesertTileEntity> findByGame(GameEntity game);
    void deleteByOwner(PlayerEntity owner);
}

package tuc.isse.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tuc.isse.entities.GameEntity;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<GameEntity, Long> {
    // find all where isPrivate is true
    Optional<GameEntity> findByRoomCode(String roomCode);

    @Query("SELECT g FROM GameEntity g WHERE g.isPrivate = false")
    List<GameEntity> findAllPublic();
}

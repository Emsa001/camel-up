package tuc.isse.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tuc.isse.entities.BettingCardEntity;

public interface BettingCardRepository extends JpaRepository<BettingCardEntity, Long> {
}

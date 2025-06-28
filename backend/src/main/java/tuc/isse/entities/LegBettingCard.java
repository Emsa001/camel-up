package tuc.isse.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "leg_betting_cards")
@Getter
@Setter
@NoArgsConstructor
public class LegBettingCard extends BettingCardEntity {

    @Column(name = "label")
    private Integer label;

    public LegBettingCard(CamelEntity camel, Integer value, GameEntity game) {
        super(camel, game);
        this.label = value;
    }
}
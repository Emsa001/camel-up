package tuc.isse.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "race_betting_cards")
@Getter
@Setter
@NoArgsConstructor
public class RaceBettingCard extends BettingCardEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "bet_type")
    private BetType betType;

    @Column(name = "order_value")
    private Integer order;

    public RaceBettingCard(CamelEntity camel, Integer order, GameEntity game) {
        super(camel, game);
        this.order = order;
    }

    public enum BetType {
        WINNER,
        LOSER
    }
}
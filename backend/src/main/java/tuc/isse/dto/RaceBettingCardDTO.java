package tuc.isse.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tuc.isse.entities.CamelEntity;
import tuc.isse.entities.RaceBettingCard;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RaceBettingCardDTO {
    private Long id;
    private Integer order;
    private Integer worth;
    private CamelEntity.CAMEL_COLOR camel;
    private Long playerId;
    private RaceBettingCard.BetType betType;

    public RaceBettingCardDTO(RaceBettingCard bettingCard) {
        this.id = bettingCard.getId();
        this.order = bettingCard.getOrder();
        this.worth = bettingCard.getWorth();
        this.camel = bettingCard.getCamel().getColor();
        this.playerId = bettingCard.getPlayer() != null ? bettingCard.getPlayer().getId() : null;
        this.betType = bettingCard.getBetType();
    }

    public RaceBettingCardDTO(Long playerId, CamelEntity.CAMEL_COLOR camelColor, RaceBettingCard.BetType betType) {
        this.camel = camelColor;
        this.playerId = playerId;
        this.betType = betType;
    }
}

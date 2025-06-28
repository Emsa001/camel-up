package tuc.isse.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tuc.isse.entities.BettingCardEntity;
import tuc.isse.entities.CamelEntity;
import tuc.isse.entities.LegBettingCard;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LegBettingCardDTO {
    private Long id;
    private Integer label;
    private Integer worth;
    private CamelEntity.CAMEL_COLOR camel;
    private Long playerId;

    public LegBettingCardDTO(LegBettingCard bettingCard) {
        this.id = bettingCard.getId();
        this.label = bettingCard.getLabel();
        this.worth = bettingCard.getWorth();
        this.camel = bettingCard.getCamel().getColor();
        this.playerId = bettingCard.getPlayer() != null ? bettingCard.getPlayer().getId() : null;
    }

    public LegBettingCardDTO(Long PlayerId, CamelEntity.CAMEL_COLOR camelColor) {
        this.camel = camelColor;
        this.playerId = PlayerId;
    }
}

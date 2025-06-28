package tuc.isse.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tuc.isse.entities.CamelEntity;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CamelDTO {
    private CamelEntity.CAMEL_COLOR color;

    // field position (position % 16)
    private int position;
    // position that is not modulo 16
    private int rawPosition;
    // position in the stack (1-5)
    private int stackPosition;
    // last roll of the camel
    private int lastRoll;

    private Boolean moved;
    private Boolean legWinner;
    private Boolean raceWinner;

    public CamelDTO(CamelEntity camel) {
        this.color = camel.getColor();
        this.position = camel.getPosition();
        this.rawPosition = camel.getRawPosition();
        this.stackPosition = camel.getStackPosition();
        this.lastRoll = camel.getLastRoll();
        this.moved = camel.getMoved();
        this.legWinner = camel.getLegWinner();
        this.raceWinner = camel.getRaceWinner();
    }
}

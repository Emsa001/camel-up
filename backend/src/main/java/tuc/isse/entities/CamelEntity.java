package tuc.isse.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "camels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CamelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private CAMEL_COLOR color;
    private int position;
    private int rawPosition;
    private int stackPosition;
    private int lastRoll;

    private Boolean moved;
    private Boolean legWinner;
    private Boolean raceWinner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    @JsonIgnore
    private GameEntity game;

    // Constructors

    public CamelEntity(CAMEL_COLOR color, GameEntity game) {
        this.setColor(color);
        this.setPosition((int) (Math.random() * 3) + 1);
        this.setRawPosition(this.getPosition());
        this.setLastRoll(0);
        this.setMoved(false);
        this.setStackPosition(0);
        this.setLegWinner(false);
        this.setRaceWinner(false);
        this.setGame(game);
    }

    public CamelEntity(CAMEL_COLOR color, GameEntity game, int position, int stackPosition) {
        this.setColor(color);
        this.setPosition(position);
        this.setRawPosition(position);
        this.setLastRoll(0);
        this.setMoved(false);
        this.setStackPosition(stackPosition);
        this.setLegWinner(false);
        this.setRaceWinner(false);
        this.setGame(game);
    }

    // Enums

    public enum CAMEL_COLOR {
        BLUE,
        YELLOW,
        GREEN,
        ORANGE,
        WHITE
    }
}

package tuc.isse.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "dice")
public class DiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dice_value")
    private Integer value;

    @ManyToOne
    @JoinColumn(name = "camel_id")
    @JsonIgnore
    private CamelEntity camel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    @JsonIgnore
    private GameEntity game;

    public DiceEntity(CamelEntity camel, GameEntity game) {
        this.value = null;
        this.game = game;
        this.camel = camel;
    }

    // methods

    public void roll() {
        this.value = (int) (Math.random() * 3) + 1;
    }
}
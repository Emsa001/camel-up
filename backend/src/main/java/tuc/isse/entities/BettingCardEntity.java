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
@Table(name = "betting_cards")
public class BettingCardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "worth")
    private Integer worth;

    @ManyToOne
    @JoinColumn(name = "camel_id")
    @JsonIgnore
    private CamelEntity camel;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private PlayerEntity player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    @JsonIgnore
    private GameEntity game;

    public BettingCardEntity(CamelEntity camel, GameEntity game) {
        this.camel = camel;
        this.game = game;
    }
}
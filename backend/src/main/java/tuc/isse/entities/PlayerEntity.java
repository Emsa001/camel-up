package tuc.isse.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
public class PlayerEntity {
    public static final int INITIAL_MONEY = 3;
    public static final int MAX_NAME_LENGTH = 12;
    public static final int MIN_NAME_LENGTH = 3;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer money;
    private Integer age;
    private Character character;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private GameEntity game;

    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private DesertTileEntity desertTile;

    public PlayerEntity(String name, Integer age) {
        this.name = name;
        this.age = age;
        this.money = INITIAL_MONEY;
        this.character = null; // Character is initially null
    }

    public PlayerEntity(String name, Integer age, GameEntity game) {
        this.name = name;
        this.age = age;
        this.money = INITIAL_MONEY;
        this.game = game;
    }

    public enum Character {
        ALICE_WEIDEL,
        ANGELA_MERKEL,
        BORIS_PISTORIUS,
        CHRISTIAN_LINDNER,
        KARL_LAUTERBACH,
        MARKUS_SOEDER,
        OLAF_SCHOLZ,
        PHILIPP_AMTHOR,
    }
}
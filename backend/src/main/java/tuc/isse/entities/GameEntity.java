package tuc.isse.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GameEntity {
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 8;
    public static final int MAX_PUBLIC_GAMES = 50;

    public static final int MAX_LOBBYNAME_LENGTH = 12;
    public static final int MIN_LOBBYNAME_LENGTH = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String roomCode;
    private Boolean isPrivate = false;

    // The current round number in the game
    private int round;
    // The current turn number in the game
    private int turn;
    // block actions flag (for waiting events)
    private boolean blockActions;


    @Enumerated(EnumType.STRING)
    private GameState state = GameState.WAITING;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "current_player_id")
    private PlayerEntity currentPlayer;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "host_id")
    private PlayerEntity host;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PlayerEntity> players = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CamelEntity> camels = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<LegBettingCard> legBettingCards = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RaceBettingCard> raceBettingCards = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DesertTileEntity> desertTiles = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DiceEntity> dices = new ArrayList<>();

    // Enums

    public enum GameState {
        WAITING,
        IN_PROGRESS,
        FINISHED
    }
}

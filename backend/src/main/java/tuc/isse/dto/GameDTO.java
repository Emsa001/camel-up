package tuc.isse.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tuc.isse.entities.GameEntity;
import tuc.isse.entities.RaceBettingCard;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameDTO {
    private Long id;
    private String name;
    private String roomCode;
    private String state;
    private Boolean isPrivate;
    private int round;
    private int turn;
    private Long currentPlayerId;
    private Long hostId;

    private List<PlayerDTO> players;
    private List<CamelDTO> camels;
    private List<LegBettingCardDTO> legBettingCards;
    private List<RaceBettingCardDTO> raceBettingCards;
    private List<DesertTileDTO> desertTiles;

    public GameDTO(GameEntity game){
        this.id = game.getId();
        this.name = game.getName();
        this.roomCode = game.getRoomCode();
        this.isPrivate = game.getIsPrivate();
        this.round = game.getRound();
        this.turn = game.getTurn();
        this.state = game.getState().name();
        this.currentPlayerId = game.getCurrentPlayer() != null ? game.getCurrentPlayer().getId() : null;
        this.hostId = game.getHost() != null ? game.getHost().getId() : null;

        this.players = game.getPlayers().stream()
                .map(PlayerDTO::new)
                .collect(Collectors.toList());

        this.camels = game.getCamels().stream()
                .map(CamelDTO::new)
                .collect(Collectors.toList());

        this.legBettingCards = game.getLegBettingCards().stream()
                .map(LegBettingCardDTO::new)
                .collect(Collectors.toList());

        this.raceBettingCards = game.getRaceBettingCards().stream()
                .map(RaceBettingCardDTO::new)
                .collect(Collectors.toList());

        this.desertTiles = game.getDesertTiles().stream().
                map(DesertTileDTO::new)
                .collect(Collectors.toList());
    }
}

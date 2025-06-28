package tuc.isse.controllers.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import tuc.isse.dto.LegBettingCardDTO;
import tuc.isse.dto.DesertTileDTO;
import tuc.isse.dto.PlayerDTO;
import tuc.isse.dto.RaceBettingCardDTO;
import tuc.isse.services.game.GameActionsService;

/**
 * Controller for handling game actions such as dice rolls, camel movements, and betting.
 */
@Controller
public class GameActionsController extends GameController {

    @Autowired
    GameActionsService gameActionsService;

    /**
     * Handles the dice roll action for a player in a specific game room.
     *
     * @param roomCode The code of the game room.
     * @param player The player who is rolling the dice.
     */
    @MessageMapping("{roomCode}/dice")
    public void handleDiceRoll(@DestinationVariable String roomCode, PlayerDTO player){
        gameActionsService.handleDiceRoll(roomCode, player);
    }

    /**
     * Receives tile updates from clients and processes them.
     *
     * @param roomCode The code of the game room.
     * @param field The field entity containing the tile update information.
     */
    @MessageMapping("{roomCode}/tiles")
    public void receiveTileUpdate(@DestinationVariable String roomCode, DesertTileDTO field){
        gameActionsService.receiveTileUpdate(roomCode, field);
    }

    /**
     * Handles player leg bets in a specific game room.
     *
     * @param roomCode The code of the game room.
     * @param card The betting card entity containing the bet information.
     */
    @MessageMapping("{roomCode}/leg-bet") // Client sends to /app/{roomCode}/leg-bet
    public void handlePlayerLegBet( @DestinationVariable String roomCode, LegBettingCardDTO card){
        gameActionsService.handlePlayerLegBet(roomCode, card);
    }

    /**
     * Handles player race bets in a specific game room.
     *
     * @param roomCode The code of the game room.
     * @param card The betting card entity containing the bet information.
     */
    @MessageMapping("{roomCode}/race-bet") // Client sends to /app/{roomCode}/leg-bet
    public void handlePlayerRaceBet(@DestinationVariable String roomCode, RaceBettingCardDTO card){
        gameActionsService.handlePlayerRaceBet(roomCode, card);
    }
}

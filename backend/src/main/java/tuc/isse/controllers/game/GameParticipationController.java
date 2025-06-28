package tuc.isse.controllers.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tuc.isse.api.GameParticipationApi;
import tuc.isse.schemas.CreatePlayerRequest;
import tuc.isse.dto.PlayerDTO;
import tuc.isse.services.game.GameParticipationService;

/**
 * This controller handles requests related to joining and leaving games.
 * It uses the GameParticipationService to process the game participation logic.
 */
@RestController
public class GameParticipationController extends GameController implements GameParticipationApi {

    @Autowired
    GameParticipationService gameParticipationService;

    /**
     * Allows a player to join a game using the provided room code and player information.
     *
     * @param roomCode The unique code of the game room.
     * @param player   The player information to join the game with.
     * @return ResponseEntity indicating the result of the operation.
     */
    @PostMapping("join/{roomCode}")
    public ResponseEntity<?> joinGame(@PathVariable String roomCode, @RequestBody CreatePlayerRequest player) {
        return gameParticipationService.joinGame(roomCode, player);
    }

    /**
     * Allows a player to leave a game using the provided player information.
     *
     * @param player The player information to leave the game with.
     * @return ResponseEntity indicating the result of the operation.
     */
    @PostMapping("leave")
    public ResponseEntity<?> leaveGame(@RequestBody PlayerDTO player) {
        return gameParticipationService.leaveGame(player);
    }
}

package tuc.isse.controllers.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tuc.isse.api.GameLifecycleApi;
import tuc.isse.dto.PlayerDTO;
import tuc.isse.services.game.GameLifecycleService;

/**
 * Controller for managing the lifecycle of a game.
 * This controller handles starting and restarting the game.
 */
@RestController
public class GameLifecycleController extends GameController implements GameLifecycleApi {

    @Autowired
    GameLifecycleService gameLifecycleService;

    /**
     * Starts a new game with the provided player information.
     *
     * @param player The player information to start the game with.
     * @return ResponseEntity indicating the result of the operation.
     */
    @PostMapping("/start")
    public ResponseEntity<?> startGame(@RequestBody PlayerDTO player) {
        return gameLifecycleService.startGame(player);
    }

    /**
     * Restarts the game with the provided player information.
     *
     * @param player The player information to restart the game with.
     * @return ResponseEntity indicating the result of the operation.
     */
    @PostMapping("/restart")
    public ResponseEntity<?> restartGame(@RequestBody PlayerDTO player) {
        return gameLifecycleService.restartGame(player);
    }
}

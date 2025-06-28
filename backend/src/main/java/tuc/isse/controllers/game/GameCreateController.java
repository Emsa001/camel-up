package tuc.isse.controllers.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tuc.isse.api.GameCreateApi;
import tuc.isse.schemas.CreateGameRequest;
import tuc.isse.services.game.GameCreationService;

/**
 * This controller handles the creation of new games.
 * It uses the GameCreationService to process the game creation logic.
 */
@RestController
public class GameCreateController extends GameController implements GameCreateApi {

    @Autowired
    GameCreationService createGameService;

    /**
     * Endpoint to create a new game.
     *
     * @param gameRequest The request object containing the game creation details.
     * @return ResponseEntity with the result of the game creation.
     */
    @PostMapping("/create")
    public ResponseEntity<Object> createGame(@RequestBody CreateGameRequest gameRequest) {
        return createGameService.createGame(gameRequest);
    }
}

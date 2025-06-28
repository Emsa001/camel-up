package tuc.isse.controllers.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tuc.isse.api.GameListApi;
import tuc.isse.dto.GameDTO;
import tuc.isse.services.game.GameListService;

import java.util.List;

/**
 * This controller handles requests related to listing games.
 * It uses the GameListService to process the retrieving game lists logic.
 */
@RestController
public class GameListController extends GameController implements GameListApi {

    @Autowired
    GameListService gameListService;

    /**
     * Retrieves a list of all games.
     *
     * @return ResponseEntity containing a list of GameDTO objects.
     */
    @GetMapping("/all")
    public ResponseEntity<List<GameDTO>> getAllGames() {
        return gameListService.getAllGames();
    }

    /**
     * Retrieves a specific game by its room code.
     *
     * @param roomCode The unique code of the game room.
     * @return ResponseEntity containing the GameDTO object for the specified room code.
     */
    @GetMapping("/{roomCode}")
    public ResponseEntity<?> getGame(@PathVariable String roomCode) {
        return gameListService.getGameByRoomCode(roomCode);
    }

    /**
     * Retrieves a list of players in a specific game room.
     *
     * @param roomCode The unique code of the game room.
     * @return ResponseEntity containing a list of players in the specified game room.
     */
    @GetMapping("/{roomCode}/players")
    public ResponseEntity<?> getPlayers(@PathVariable String roomCode) {
        return gameListService.getPlayersByRoomCode(roomCode);
    }
}
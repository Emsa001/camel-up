package tuc.isse.services.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tuc.isse.schemas.ErrorResponse;
import tuc.isse.dto.GameDTO;
import tuc.isse.entities.GameEntity;
import tuc.isse.repositories.GameRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing game lists.
 * Provides methods to retrieve all public games and a specific game by room code.
 */
@Service
public class GameListService {

    @Autowired
    GameRepository gameRepository;

    /**
     * Retrieves a list of all public games.
     *
     * @return ResponseEntity containing a list of GameEntity objects.
     */
    public ResponseEntity<List<GameDTO>> getAllGames() {
        List<GameDTO> games = gameRepository.findAllPublic()
                .stream()
                .map(GameDTO::new)
                .toList();
        return ResponseEntity.ok(games);
    }

    /**
     * Retrieves a specific game by its room code.
     *
     * @param roomCode The unique code of the game room.
     * @return ResponseEntity containing the GameEntity if found, or an error response if not found.
     */
    public ResponseEntity<?> getGameByRoomCode(String roomCode) {
        Optional<GameEntity> optionalGame = gameRepository.findByRoomCode(roomCode);

        if (optionalGame.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Game not found"));
        }
        return ResponseEntity.ok(new GameDTO(optionalGame.get()));
    }

    /**
     * Retrieves a list of players in a game by its room code.
     *
     * @param roomCode The unique code of the game room.
     * @return ResponseEntity containing a list of PlayerEntity objects if found, or an error response if not found.
     */
    public ResponseEntity<?> getPlayersByRoomCode(String roomCode) {
        Optional<GameEntity> game = gameRepository.findByRoomCode(roomCode);

        if (game.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Game not found"));

        return ResponseEntity.ok(game.get().getPlayers());
    }
}
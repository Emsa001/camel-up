package tuc.isse.services.game;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import tuc.isse.schemas.ErrorResponse;
import tuc.isse.dto.GameDTO;
import tuc.isse.dto.PlayerDTO;
import tuc.isse.entities.GameEntity;
import tuc.isse.entities.PlayerEntity;
import tuc.isse.repositories.GameRepository;
import tuc.isse.repositories.PlayerRepository;
import tuc.isse.utils.Logger;

/**
 * Service for managing the lifecycle of a game.
 * This service handles starting and restarting the game.
 */
@Service
public class GameLifecycleService {

    @Autowired
    GameInitService gameInitService;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    GameSocketService gameSocketService;

    /**
     * Starts a new game with the given game room code and player.
     *
     * @param requestPlayer the player who is starting the game
     * @return ResponseEntity indicating the result of the operation
     */
    @Transactional
    public ResponseEntity<?> startGame(@RequestBody PlayerDTO requestPlayer) {
        // 1. Validate the game room code
        GameEntity game = gameRepository.findById(requestPlayer.getGameId()).orElse(null);
        if (game == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Game not found"));
        }

        // 2. Validate the player
        PlayerEntity player = playerRepository.findById(requestPlayer.getId()).orElse(null);
        if (player == null || !game.getHost().getId().equals((player.getId()))) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Player is not the host of the game"));
        }

        // 3. Check if the game is already started
        if (game.getState() != GameEntity.GameState.WAITING) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Game is already started or finished"));
        }

        // 4. Validate that all players have selected their characters
        if (game.getPlayers().stream().anyMatch(p -> p.getCharacter() == null)) {
            return ResponseEntity.badRequest().body(new ErrorResponse("All players must select a character before starting the game"));
        }

        // 5. Start the game
        game.setState(GameEntity.GameState.IN_PROGRESS);
        gameInitService.init(game);

        // 6. Save the game state
        gameRepository.save(game);

        // 8. Notify players about the game start
        gameSocketService.roomUpdate(game);
        gameSocketService.lobbyUpdate(game);

        // 8. Log the game start
        Logger.info("Game started with room code: " + game.getRoomCode() + " by player: " + player.getName());

        return ResponseEntity.ok(new GameDTO(game));
    }

    /**
     * Restarts the game with the given game room code.
     * @param requestPlayer the player who is restarting the game
     * @return ResponseEntity indicating the result of the operation
     */
    @Transactional
    public ResponseEntity<?> restartGame(@RequestBody PlayerDTO requestPlayer) {
        // 1. Validate the game room code
        GameEntity game = gameRepository.findById(requestPlayer.getGameId()).orElse(null);
        if (game == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Game not found"));
        }

        // 2. Validate the player
        PlayerEntity player = playerRepository.findById(requestPlayer.getId()).orElse(null);
        if (player == null || !game.getHost().getId().equals((player.getId()))) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Player is not the host of the game"));
        }

        /**
        // 3. Check if the game is in a state that can be restarted
        if (game.getState() != GameEntity.GameState.FINISHED) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Game can only be restarted from the finished state"));
        }
        */

        // 4.1. Reset the game state
        game.setState(GameEntity.GameState.WAITING);

        // 4.2. Reinitialize the game
        gameInitService.init(game);

        // 5. Save the game state
        gameRepository.save(game);

        // 6. Notify players about the game restart
        gameSocketService.roomUpdate(game);
        gameSocketService.lobbyUpdate(game);

        // 7. Log the game restart
        Logger.info("Game restarted with room code: " + game.getRoomCode());

        return ResponseEntity.ok(new GameDTO(game));
    }

}

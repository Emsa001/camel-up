package tuc.isse.services.game;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import tuc.isse.schemas.CreatePlayerRequest;
import tuc.isse.schemas.ErrorResponse;
import tuc.isse.dto.GameDTO;
import tuc.isse.dto.PlayerDTO;
import tuc.isse.entities.GameEntity;
import tuc.isse.entities.PlayerEntity;
import tuc.isse.repositories.GameRepository;
import tuc.isse.repositories.PlayerRepository;
import tuc.isse.services.player.PlayerTilesService;
import tuc.isse.utils.Logger;

/**
 * This service handles the logic for players joining and leaving games.
 * It interacts with the GameRepository and PlayerRepository to save and manage game and player entities.
 */
@Service
public class GameParticipationService {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    GameSocketService gameSocketService;

    @Autowired
    PlayerTilesService playerTilesService;

    @Autowired
    GameRoundService gameRoundService;

    /**
     * Allows a player to join a game if the game exists, is not full, and is in the waiting state.
     * If the player is already in the game or if the game has started, an error response is returned.
     *
     * @param roomCode      The code of the game room.
     * @param playerRequest The player entity trying to join the game.
     * @return ResponseEntity with the updated game or an error message.
     */
    @Transactional
    public ResponseEntity<?> joinGame(@PathVariable String roomCode, @RequestBody CreatePlayerRequest playerRequest) {
        // 1. Find the game by room code
        GameEntity game = gameRepository.findByRoomCode(roomCode).orElse(null);
        if (game == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Game not found"));
        }

        // 2. Check if the game is full
        if (game.getPlayers().size() >= GameEntity.MAX_PLAYERS) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Game is full"));
        }

        // 3. Check if the game is in the waiting state
        if (game.getState() != GameEntity.GameState.WAITING) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Game has already started"));
        }

        // 4.1 Check if player name is in correct format
        if (playerRequest.getName() == null
                || playerRequest.getName().isEmpty()
                || !playerRequest.getName().matches("^[A-Za-z0-9_]+$")) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid player name"));
        }
        else if (playerRequest.getName() == null
                || playerRequest.getName().length() > PlayerEntity.MAX_NAME_LENGTH) {
            return ResponseEntity.badRequest().body(new ErrorResponse("name is too long (max. " + PlayerEntity.MAX_NAME_LENGTH + " characters)"));
        }
        else if (playerRequest.getName() == null
                || playerRequest.getName().length() < PlayerEntity.MIN_NAME_LENGTH) {
            return ResponseEntity.badRequest().body(new ErrorResponse("name is too short (min. " + PlayerEntity.MIN_NAME_LENGTH + " characters)"));
        }

        // 4.2. Check if the player name is available
        if (game.getPlayers().stream().anyMatch(p -> p.getName().equals(playerRequest.getName()))) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Player name already taken"));
        }

        // 5. Create player entity
        PlayerEntity player = new PlayerEntity(playerRequest.getName(), playerRequest.getAge());

        // 6.1. Add the player to the game
        player.setGame(game);
        game.getPlayers().add(player);

        // 6.2. Save the game and player entities
        playerRepository.save(player);

        // 7. Send a websocket message to notify other players
        gameSocketService.lobbyUpdate(game);
        gameSocketService.roomUpdate(game);

        // 8. Log the player joining the game
        Logger.info(player.getName() + " (ID: " + player.getId() + ") joined a " +
                (game.getIsPrivate() ? "PRIVATE" : "PUBLIC") + " game with room code: " + roomCode);

        return ResponseEntity.ok(new GameDTO(game));
    }

    /**
     * Allows a player to leave a game if they are currently in it.
     * If the game does not exist or the player is not in the game, an error response is returned.
     *
     * @param requestPlayer The player entity trying to leave the game.
     * @return ResponseEntity with the updated game or an error message.
     */
    @Transactional
    public ResponseEntity<?> leaveGame(@RequestBody PlayerDTO requestPlayer) {
        GameEntity game = gameRepository.findById(requestPlayer.getGameId()).orElse(null);
        if (game == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Game not found"));
        }

        PlayerEntity player = playerRepository.findById(requestPlayer.getId()).orElse(null);
        if (player == null || !game.getPlayers().contains(player)) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Player not in game"));
        }

        // Set the new game host if the player leaving was the host
        if (game.getHost() != null && game.getHost().getId().equals(player.getId())) {
            PlayerEntity newHost = game.getPlayers().stream()
                    .filter(p -> !p.getId().equals(player.getId()))
                    .findFirst().orElse(null);
            game.setHost(newHost);
            Logger.log("New host set for game with room code: " + game.getRoomCode() + " - New Host: " + (newHost != null ? newHost.getName() : "None"));
        }

        // Remove current player from the game if it's their turn
        if (game.getCurrentPlayer() != null && game.getCurrentPlayer().getId().equals(player.getId())) {
            gameRoundService.endTurn(player);
        }

        playerTilesService.deletePlayerTiles(player);
        game.getPlayers().remove(player);
        player.setGame(null);

        // If the number of players drops below the minimum, end the game
        if (game.getState() == GameEntity.GameState.IN_PROGRESS && game.getPlayers().size() < GameEntity.MIN_PLAYERS) {
            game.setState(GameEntity.GameState.FINISHED);
            Logger.warn("Game with room code: " + game.getRoomCode() + " has been ended due to insufficient players.");
        }

        if (game.getPlayers().isEmpty()) {
            gameSocketService.lobbyUpdate(game);

            gameRepository.delete(game);
            Logger.warn("Game with room code: " + game.getRoomCode() + " has been deleted as it is empty.");
            return ResponseEntity.ok(new ErrorResponse("Game deleted as it is empty"));
        }


        gameRepository.save(game);

        gameSocketService.lobbyUpdate(game);
        gameSocketService.roomUpdate(game);

        Logger.warn(player.getName() + " (ID: " + player.getId() + ") left the " +
                (game.getIsPrivate() ? "PRIVATE" : "PUBLIC") + " game with room code: " + game.getRoomCode());

        if(game.getPlayers().size() < GameEntity.MIN_PLAYERS) {
            gameRoundService.gameEnd(game);
        }

        return ResponseEntity.ok(new GameDTO(game));
    }
}
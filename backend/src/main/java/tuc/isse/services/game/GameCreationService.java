package tuc.isse.services.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tuc.isse.schemas.CreateGameRequest;
import tuc.isse.schemas.CreatePlayerRequest;
import tuc.isse.schemas.ErrorResponse;
import tuc.isse.entities.GameEntity;
import tuc.isse.entities.PlayerEntity;
import tuc.isse.repositories.GameRepository;
import tuc.isse.utils.Logger;

/**
 * This service handles the creation of new games.
 * It interacts with the GameRepository to save game entities
 */
@Service
public class GameCreationService {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    GameSocketService gameSocketService;

    /**
     * Creates a new game based on the provided game request.
     *
     * @param gameRequest The request containing game details and host player information.
     * @return ResponseEntity with the created game entity or an error response.
     */
    @Transactional
    public ResponseEntity<Object> createGame(CreateGameRequest gameRequest) {
        if (gameRequest.getName() == null
                || gameRequest.getName().isEmpty()
                || !gameRequest.getName().matches("^[A-Za-z0-9_]+$")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Invalid lobbyname"));
        }
        else if (gameRequest.getName() == null
                || gameRequest.getName().length() > GameEntity.MAX_LOBBYNAME_LENGTH) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("lobbyname is too long (max. " + GameEntity.MAX_LOBBYNAME_LENGTH + " characters)"));
        }
        else if (gameRequest.getName() == null
                || gameRequest.getName().length() < GameEntity.MIN_LOBBYNAME_LENGTH) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("lobbyname is too short (min. " + GameEntity.MIN_LOBBYNAME_LENGTH + " characters)"));

        // 1. Validate the game request for limitations
        }
        if (!gameRequest.getIsPrivate() && gameRepository.findAll().size() >= GameEntity.MAX_PUBLIC_GAMES) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Maximum number of public games reached"));
        }

        // 2. Create a new game entity
        GameEntity game = new GameEntity();

        // 3. Generate a unique room code
        String roomCode = String.format("%06d", (int) (Math.random() * 1_000_000));
        if (gameRepository.findByRoomCode(roomCode).isPresent()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to generate a unique room code"));
        }
        game.setRoomCode(roomCode);

        // 4. Set game properties from the request
        game.setIsPrivate(gameRequest.getIsPrivate());
        game.setName(gameRequest.getName());


        // 5.1. Create the host player
        CreatePlayerRequest playerRequest = gameRequest.getHost();
        PlayerEntity host = new PlayerEntity(playerRequest.getName(), playerRequest.getAge());

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

        // 5.2. Save the host player
        game.setHost(host);
        game.getPlayers().add(host);
        host.setGame(game);


        // 6. Save the game entity
        gameRepository.save(game);

        // 7. Send a websocket message to notify about the new game creation
        if(!game.getIsPrivate())
            gameSocketService.lobbyUpdate(game);

        // 8. Log the game creation
        Logger.info(host.getName() + " (ID: " + host.getId() + ") created a new " +
                (game.getIsPrivate() ? "PRIVATE" : "PUBLIC") + " game with room code: " + roomCode);

        return ResponseEntity.status(HttpStatus.CREATED).body(game);
    }
}
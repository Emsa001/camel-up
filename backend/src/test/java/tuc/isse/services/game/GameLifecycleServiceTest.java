package tuc.isse.services.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import tuc.isse.dto.GameDTO;
import tuc.isse.dto.PlayerDTO;
import tuc.isse.entities.GameEntity;
import tuc.isse.entities.PlayerEntity;
import tuc.isse.repositories.GameRepository;
import tuc.isse.repositories.PlayerRepository;
import tuc.isse.schemas.ErrorResponse;
import tuc.isse.utils.Logger;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GameLifecycleServiceTest {

    @Autowired
    GameLifecycleService gameLifecycleService;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    PlayerRepository playerRepository;

    private GameEntity createGame(PlayerEntity host) {
        GameEntity game = new GameEntity();
        game.setHost(host);
        game.setState(GameEntity.GameState.WAITING);
        game.setRoomCode("ROOM1");
        game.getPlayers().add(host);
        return gameRepository.save(game);
    }

    @BeforeEach
    void cleanUp() {
        gameRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @Test
    void startGame_success() {
        PlayerEntity host = new PlayerEntity("Host", 20);
        host.setCharacter(PlayerEntity.Character.ALICE_WEIDEL);
        GameEntity game = createGame(host);

        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setId(host.getId());
        playerDTO.setGameId(game.getId());

        ResponseEntity<?> response = gameLifecycleService.startGame(playerDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof GameDTO);
    }

    @Test
    void startGame_gameNotFound() {
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setId(1L);
        playerDTO.setGameId(99L);

        ResponseEntity<?> response = gameLifecycleService.startGame(playerDTO);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }

    @Test
    void restartGame_success() {
        PlayerEntity host = new PlayerEntity("Host", 20);
        GameEntity game = createGame(host);

        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setId(host.getId());
        playerDTO.setGameId(game.getId());

        ResponseEntity<?> response = gameLifecycleService.restartGame(playerDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof GameDTO);
    }

    @Test
    void restartGame_gameNotFound() {
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setId(1L);
        playerDTO.setGameId(99L);

        ResponseEntity<?> response = gameLifecycleService.restartGame(playerDTO);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }

    @Test
    void test_youngestPlayerStarts(){
        PlayerEntity player1 = new PlayerEntity("Alice", 25);
        player1.setCharacter(PlayerEntity.Character.ALICE_WEIDEL);
        PlayerEntity player2 = new PlayerEntity("Bob", 20);
        player2.setCharacter(PlayerEntity.Character.ANGELA_MERKEL);
        PlayerEntity player3 = new PlayerEntity("Charlie", 30);
        player3.setCharacter(PlayerEntity.Character.KARL_LAUTERBACH);

        GameEntity game = createGame(player1);

        player1.setGame(game);
        player2.setGame(game);
        player3.setGame(game);

        game.setHost(player1);

        // Add all players to the game's player list
        game.getPlayers().add(player2);
        game.getPlayers().add(player3);

        playerRepository.save(player1);
        playerRepository.save(player2);
        playerRepository.save(player3);

        gameRepository.save(game);

        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setId(player1.getId());
        playerDTO.setGameId(game.getId());

        ResponseEntity<?> response = gameLifecycleService.startGame(playerDTO);
        Logger.error("Response: " + response.getBody());

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(game.getCurrentPlayer().getId(), player2.getId(), "Youngest player should start the game");
    }
}
package tuc.isse.services.game;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tuc.isse.entities.GameEntity;
import tuc.isse.entities.PlayerEntity;
import tuc.isse.repositories.GameRepository;
import tuc.isse.repositories.PlayerRepository;
import tuc.isse.utils.Logger;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GameRoundServiceTest {

    @Autowired
    GameLifecycleService gameLifecycleService;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    GameInitService gameInitService;
    @Autowired
    GameRoundService gameRoundService;

    private GameEntity createGame() {
        GameEntity game = new GameEntity();
        game.setState(GameEntity.GameState.IN_PROGRESS);
        game.setRoomCode("ROOM1");
        return gameRepository.save(game);
    }

    @Test
    @Transactional
    void testGameTurn() {
        GameEntity game = createGame();

        PlayerEntity player1 = new PlayerEntity("Player1", 20, game);
        PlayerEntity player2 = new PlayerEntity("Player2", 10, game);
        PlayerEntity player3 = new PlayerEntity("Player3", 23, game);
        PlayerEntity player4 = new PlayerEntity("Player4", 8, game);
        PlayerEntity player5 = new PlayerEntity("Player5", 15, game);
        PlayerEntity player6 = new PlayerEntity("Player6", 30, game);
        PlayerEntity player7 = new PlayerEntity("Player7", 25, game);
        PlayerEntity player8 = new PlayerEntity("Player8", 18, game);

        game.getPlayers().add(player1);
        game.getPlayers().add(player2);
        game.getPlayers().add(player3);
        game.getPlayers().add(player4);
        game.getPlayers().add(player5);
        game.getPlayers().add(player6);
        game.getPlayers().add(player7);
        game.getPlayers().add(player8);

        List<PlayerEntity> order = game.getPlayers().stream()
                .sorted(Comparator.comparing(PlayerEntity::getAge))
                .toList();

        for( PlayerEntity player : order) {
            player.setGame(game);
            Logger.log("Adding player: " + player.getName() + " with age: " + player.getAge());
        }

        playerRepository.flush();
        gameRepository.save(game);

        // 5. Start the game
        game.setState(GameEntity.GameState.IN_PROGRESS);
        gameInitService.init(game);

        // 6. Save the game state
        gameRepository.save(game);

        assertEquals(game.getPlayers().size(), 8, "Game should have 8 players");
        for(int i = 0; i < 16; i++) {
            PlayerEntity shouldBePlayer = order.get(i % order.size());
            assertEquals(game.getCurrentPlayer().getName(), shouldBePlayer.getName(), "Round " + (i + 1) + ": Current player should be " + shouldBePlayer.getName());
            Logger.info("Player " + shouldBePlayer.getName() + "'s turn");
            gameRoundService.endTurn(shouldBePlayer);
        }
    }
}
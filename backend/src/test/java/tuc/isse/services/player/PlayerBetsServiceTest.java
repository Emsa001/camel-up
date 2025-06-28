package tuc.isse.services.player;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tuc.isse.dto.LegBettingCardDTO;
import tuc.isse.dto.RaceBettingCardDTO;
import tuc.isse.entities.*;
import tuc.isse.repositories.DesertTileRepository;
import tuc.isse.repositories.GameRepository;
import tuc.isse.repositories.PlayerRepository;
import tuc.isse.services.camel.CamelMovementService;
import tuc.isse.services.game.GameActionsService;
import tuc.isse.services.game.GameInitService;
import tuc.isse.services.game.GameRoundService;
import tuc.isse.utils.Logger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlayerBetsServiceTest {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private CamelMovementService camelMovementService;

    @Autowired
    private DesertTileRepository desertTileRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerBetsService playerBetsService;

    @Autowired
    private GameInitService gameInitService;

    @Autowired
    private GameRoundService gameRoundService;

    @Autowired
    GameActionsService gameActionsService;

    private GameEntity createGame() {
        GameEntity game = new GameEntity();
        game.setRoomCode("Room001");
        game.setIsPrivate(false);
        game.setName("test");

        PlayerEntity player = new PlayerEntity("Player", 20);
        player.setGame(game);

        PlayerEntity botPlayer = new PlayerEntity("BotPlayer", 25);
        botPlayer.setGame(game);

        game.setHost(player);

        game.getPlayers().add(player);
        game.getPlayers().add(botPlayer);

        game.setState(GameEntity.GameState.IN_PROGRESS);
        gameInitService.init(game);

        int i = 1;
        for (CamelEntity camel : game.getCamels()) {
            camel.setPosition(0);
            camel.setRawPosition(0);
            camel.setStackPosition(i++);
        }

        gameRepository.save(game);

        return game;
    }

    /*
        1: BLUE, YELLOW, GREEN, ORANGE, WHITE
        2: -

        Orange moves 3 spaces

        1: BLUE, YELLOW, GREEN
        2: -
        3: -
        4: ORANGE, WHITE
     */
    @Test
    @Transactional
    void betLegCard1() {
        GameEntity game = createGame();
        gameRepository.save(game);

        PlayerEntity player = game.getPlayers().getFirst();
        PlayerEntity bot = game.getPlayers().getLast();

        CamelEntity orange = game.getCamels().stream().filter(c -> c.getColor() == CamelEntity.CAMEL_COLOR.ORANGE).findFirst().orElseThrow(() -> new IllegalArgumentException("Camel not found"));

        CamelEntity white = game.getCamels().stream().filter(c -> c.getColor() == CamelEntity.CAMEL_COLOR.WHITE).findFirst().orElseThrow(() -> new IllegalArgumentException("Camel not found"));

        // player card 1
        LegBettingCardDTO playerLegCard1 = new LegBettingCardDTO(player.getId(), orange.getColor());
        gameActionsService.handlePlayerLegBet(game.getRoomCode(), playerLegCard1);

        // bot card 1
        LegBettingCardDTO botLegCard1 = new LegBettingCardDTO(bot.getId(), white.getColor());
        gameActionsService.handlePlayerLegBet(game.getRoomCode(), botLegCard1);

        // player card 2
        LegBettingCardDTO playerLegCard2 = new LegBettingCardDTO(player.getId(), orange.getColor());
        gameActionsService.handlePlayerLegBet(game.getRoomCode(), playerLegCard2);

        // bot card 2
        LegBettingCardDTO botLegCard2 = new LegBettingCardDTO(bot.getId(), white.getColor());
        gameActionsService.handlePlayerLegBet(game.getRoomCode(), botLegCard2);

        // player card 3
        LegBettingCardDTO playerLegCard3 = new LegBettingCardDTO(player.getId(), orange.getColor());
        gameActionsService.handlePlayerLegBet(game.getRoomCode(), playerLegCard3);

        // bot card 3
        LegBettingCardDTO botLegCard3 = new LegBettingCardDTO(bot.getId(), white.getColor());
        gameActionsService.handlePlayerLegBet(game.getRoomCode(), botLegCard3);

        camelMovementService.moveCamel(orange, 3);

        int playerMoney = player.getMoney();
        int botMoney = bot.getMoney();
        gameRoundService.startNewLeg(game);

        assertEquals(playerMoney + 3, player.getMoney(), "Player's money should increase by 4 after the leg betting evaluation");

        assertEquals(botMoney + 10, bot.getMoney(), "Bot's money should increase by 10 after the leg betting evaluation");
    }

    /*
    1: BLUE, YELLOW, GREEN, ORANGE, WHITE
    2: -

    GREEN moves 16 spaces

    1: BLUE, YELLOW
    ...
    16: -
    1: GREEN, ORANGE, WHITE
 */
    @Test
    @Transactional
    void raceBettingTest(){
        GameEntity game = createGame();
        gameRepository.save(game);

        PlayerEntity player = game.getPlayers().getFirst();
        PlayerEntity bot = game.getPlayers().getLast();

        CamelEntity green = game.getCamels().stream().filter(c -> c.getColor() == CamelEntity.CAMEL_COLOR.GREEN).findFirst().orElseThrow(() -> new IllegalArgumentException("Camel not found"));

        CamelEntity orange = game.getCamels().stream().filter(c -> c.getColor() == CamelEntity.CAMEL_COLOR.ORANGE).findFirst().orElseThrow(() -> new IllegalArgumentException("Camel not found"));

        CamelEntity white = game.getCamels().stream().filter(c -> c.getColor() == CamelEntity.CAMEL_COLOR.WHITE).findFirst().orElseThrow(() -> new IllegalArgumentException("Camel not found"));

        RaceBettingCard.BetType Winner = RaceBettingCard.BetType.WINNER;
        RaceBettingCard.BetType Loser = RaceBettingCard.BetType.LOSER;

        // player card 1
        RaceBettingCardDTO playerCard1 = new RaceBettingCardDTO(player.getId(), orange.getColor(), Winner);
        gameActionsService.handlePlayerRaceBet(game.getRoomCode(), playerCard1);

        // bot card 1
        RaceBettingCardDTO botCard1 = new RaceBettingCardDTO(bot.getId(), orange.getColor(), Loser);
        gameActionsService.handlePlayerRaceBet(game.getRoomCode(), botCard1);

        // player card 2
        RaceBettingCardDTO playerCard2 = new RaceBettingCardDTO(player.getId(), green.getColor(), Winner);
        gameActionsService.handlePlayerRaceBet(game.getRoomCode(), playerCard2);

        // bot card 2
        RaceBettingCardDTO botCard2 = new RaceBettingCardDTO(bot.getId(), white.getColor(), Loser);
        gameActionsService.handlePlayerRaceBet(game.getRoomCode(), botCard2);

        // player card 3
        RaceBettingCardDTO playerCard3 = new RaceBettingCardDTO(player.getId(), white.getColor(), Winner);
        gameActionsService.handlePlayerRaceBet(game.getRoomCode(), playerCard3);

        // bot card 3
        RaceBettingCardDTO botCard3 = new RaceBettingCardDTO(bot.getId(), green.getColor(), Winner);
        gameActionsService.handlePlayerRaceBet(game.getRoomCode(), botCard3);

        camelMovementService.moveCamel(green, 16);

        gameRoundService.gameEnd(game);

        assertEquals(9, player.getMoney(), "Player's money should increase by 4 after the leg betting evaluation");
        assertEquals(0, bot.getMoney(), "Bot's money should increase by 10 after the leg betting evaluation");
    }

}
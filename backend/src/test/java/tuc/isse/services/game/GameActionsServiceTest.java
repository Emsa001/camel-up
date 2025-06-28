package tuc.isse.services.game;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import tuc.isse.dto.*;
import tuc.isse.entities.*;
import tuc.isse.entities.CamelEntity.CAMEL_COLOR;
import tuc.isse.entities.DesertTileEntity.TileType;
import tuc.isse.entities.RaceBettingCard.BetType;
import tuc.isse.repositories.*;
import tuc.isse.services.camel.CamelMovementService;
import tuc.isse.services.player.PlayerBetsService;
import tuc.isse.services.player.PlayerTilesService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameActionsServiceTest {

    @Autowired
    private GameActionsService service;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private CamelMovementService camelMovementService;

    @Autowired
    private PlayerTilesService playerTilesService;

    @Autowired
    private GameRoundService gameRoundService;

    @Autowired
    private GameSocketService gameSocketService;

    @Autowired
    private PlayerBetsService playerBetsService;

    @Autowired
    private BettingCardRepository bettingCardRepository;

    @Autowired
    private GameInitService gameInitService;

    // Helper record to encapsulate game and player setup
    private record GameSetup(GameEntity game, PlayerEntity player) {}

    /**
     * Creates a new test game with a room code and a main player plus a bot player.
     * The game is initialized and saved in the repository.
     */
    private GameSetup createTestGameWithPlayer(String roomCode) {
        GameEntity game = new GameEntity();
        game.setRoomCode(roomCode);
        game.setIsPrivate(false);
        game.setName("test");

        PlayerEntity player = new PlayerEntity("Player",20);
        player.setGame(game);

        game.setCurrentPlayer(player);
        game.getPlayers().add(player);
        game.setHost(player);

        PlayerEntity botPlayer = new PlayerEntity("BotPlayer", 25);
        botPlayer.setGame(game);
        game.getPlayers().add(botPlayer);

        game.setState(GameEntity.GameState.IN_PROGRESS);
        gameInitService.init(game);
        gameRepository.save(game);

        return new GameSetup(game, player);
    }

    // ----------------------------------------
    // TESTS
    // ----------------------------------------

    @Test
    void handleDiceRoll_happyPath() {
        String roomCode = "ROOM123";
        GameSetup setup = createTestGameWithPlayer(roomCode);
        GameEntity game = setup.game();
        PlayerEntity player = setup.player();

        int originalMoney = player.getMoney();

        service.handleDiceRoll(roomCode, new PlayerDTO(player));

        // Refresh from DB
        GameEntity updatedGame = gameRepository.findById(game.getId()).orElseThrow();
        PlayerEntity updatedPlayer = updatedGame.getPlayers().stream()
                .filter(p -> p.getId().equals(player.getId()))
                .findFirst().orElseThrow();

        // Assert dice rolled
        List<DiceEntity> rolledDices = updatedGame.getDices().stream()
                .filter(d -> d.getValue() != null)
                .toList();
        assertFalse(rolledDices.isEmpty(), "Expected at least one rolled dice");

        DiceEntity rolledDice = rolledDices.get(0);
        assertNotNull(rolledDice.getValue(), "Dice should have a value after rolling");
        assertTrue(rolledDice.getValue() >= 1 && rolledDice.getValue() <= 3, "Dice roll must be between 1 and 3");

        // Assert player money incremented
        assertEquals(originalMoney + 1, updatedPlayer.getMoney(), "Player should have gained 1 money");

        // Assert current player updated (not the same as player who rolled)
        assertNotEquals(updatedPlayer.getId(), updatedGame.getCurrentPlayer().getId(), "Current player should have changed");

        // Assert camel moved
        assertTrue(rolledDice.getCamel().getMoved(), "Camel should be marked as moved");
    }

    @Test
    void receiveTileUpdate_happyPath() {
        String roomCode = "ROOM456";
        GameSetup setup = createTestGameWithPlayer(roomCode);
        GameEntity game = setup.game();
        PlayerEntity player = setup.player();

        DesertTileDTO tileDTO = new DesertTileDTO();
        tileDTO.setGameId(game.getId());
        tileDTO.setOwnerId(player.getId());
        tileDTO.setPosition(3);
        tileDTO.setType(TileType.OASIS);

        service.receiveTileUpdate(roomCode, tileDTO);

        List<DesertTileEntity> tiles = game.getDesertTiles().stream()
                .filter(t -> t.getOwner().getId().equals(player.getId()))
                .toList();

        assertEquals(1, tiles.size(), "Player should have exactly one desert tile");
        DesertTileEntity tile = tiles.get(0);
        assertEquals(3, tile.getPosition());
        assertEquals(TileType.OASIS, tile.getType());
    }

    @Test
    void handlePlayerLegBet_playerThenBot_getsCorrectCard() {
        String roomCode = "ROOM997";
        GameSetup setup = createTestGameWithPlayer(roomCode);
        GameEntity game = setup.game();
        PlayerEntity player = setup.player();

        PlayerEntity botPlayer = game.getPlayers().stream()
                .filter(p -> !p.getId().equals(player.getId()))
                .findFirst().orElseThrow();

        CamelEntity camel = game.getCamels().get(0);

        // Player bets first
        LegBettingCardDTO playerCardDTO = new LegBettingCardDTO();
        playerCardDTO.setPlayerId(player.getId());
        playerCardDTO.setCamel(camel.getColor());

        service.handlePlayerLegBet(roomCode, playerCardDTO);

        // Bot bets second
        LegBettingCardDTO botCardDTO = new LegBettingCardDTO();
        botCardDTO.setPlayerId(botPlayer.getId());
        botCardDTO.setCamel(camel.getColor());

        service.handlePlayerLegBet(roomCode, botCardDTO);

        GameEntity updatedGame = gameRepository.findById(game.getId()).orElseThrow();

        List<LegBettingCard> camelCards = updatedGame.getLegBettingCards().stream()
                .filter(c -> c.getCamel().getColor() == camel.getColor())
                .sorted(Comparator.comparingInt(LegBettingCard::getLabel).reversed()) // descending label order
                .toList();

        LegBettingCard playerCard = camelCards.stream()
                .filter(c -> c.getPlayer() != null && c.getPlayer().getId().equals(player.getId()))
                .findFirst().orElseThrow();

        LegBettingCard botCard = camelCards.stream()
                .filter(c -> c.getPlayer() != null && c.getPlayer().getId().equals(botPlayer.getId()))
                .findFirst().orElseThrow();

        assertEquals(5, playerCard.getLabel(), "Player should get highest label (5)");
        assertEquals(player.getId(), playerCard.getPlayer().getId());

        assertEquals(3, botCard.getLabel(), "Bot should get next highest label (3)");
        assertEquals(botPlayer.getId(), botCard.getPlayer().getId());
    }

    @Test
    void handlePlayerRaceBet_botThenPlayer_getsCorrectCard() {
        String roomCode = "ROOM998";
        GameSetup setup = createTestGameWithPlayer(roomCode);
        GameEntity game = setup.game();
        PlayerEntity player = setup.player();

        PlayerEntity botPlayer = game.getPlayers().stream()
                .filter(p -> !p.getId().equals(player.getId()))
                .findFirst().orElseThrow();

        CamelEntity camel = game.getCamels().get(0);

        // Create 3 race betting cards with orders 1,2,3
        List<RaceBettingCard> cards = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            RaceBettingCard card = new RaceBettingCard();
            card.setCamel(camel);
            card.setOrder(i);
            card.setGame(game);
            cards.add(card);
        }
        game.getRaceBettingCards().addAll(cards);
        bettingCardRepository.saveAll(cards);
        gameRepository.save(game);

        // Player takes next card (order 2)
        RaceBettingCardDTO playerCardDTO = new RaceBettingCardDTO();
        playerCardDTO.setPlayerId(player.getId());
        playerCardDTO.setCamel(camel.getColor());
        playerCardDTO.setBetType(BetType.WINNER);

        service.handlePlayerRaceBet(roomCode, playerCardDTO);

        // Bot takes first card (order 1)
        RaceBettingCardDTO botCardDTO = new RaceBettingCardDTO();
        botCardDTO.setPlayerId(botPlayer.getId());
        botCardDTO.setCamel(camel.getColor());
        botCardDTO.setBetType(BetType.WINNER);

        service.handlePlayerRaceBet(roomCode, botCardDTO);

        GameEntity updatedGame = gameRepository.findById(game.getId()).orElseThrow();

        RaceBettingCard playerCard = updatedGame.getRaceBettingCards().stream()
                .filter(c -> c.getPlayer() != null && c.getPlayer().getId().equals(player.getId()))
                .findFirst().orElseThrow();

        RaceBettingCard botCard = updatedGame.getRaceBettingCards().stream()
                .filter(c -> c.getPlayer() != null && c.getPlayer().getId().equals(botPlayer.getId()))
                .findFirst().orElseThrow();

        assertNotNull(botCard.getPlayer(), "Bot should have received first card");
        assertEquals(botPlayer.getId(), botCard.getPlayer().getId());

        assertNotNull(playerCard.getPlayer(), "Player should have received second card");
        assertEquals(player.getId(), playerCard.getPlayer().getId());

        assertEquals(BetType.WINNER, playerCard.getBetType(), "Player card bet type should be WINNER");
    }
}
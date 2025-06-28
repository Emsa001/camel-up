package tuc.isse.services.camel;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tuc.isse.entities.CamelEntity;
import tuc.isse.entities.DesertTileEntity;
import tuc.isse.entities.GameEntity;
import tuc.isse.entities.PlayerEntity;
import tuc.isse.repositories.DesertTileRepository;
import tuc.isse.repositories.GameRepository;
import tuc.isse.repositories.PlayerRepository;
import tuc.isse.services.player.PlayerBetsService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CamelMovementServiceTest {

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

    private GameEntity createTestGameWithCamels() {
        GameEntity game = new GameEntity();
        game.setRoomCode("ROOM001");
        game.setState(GameEntity.GameState.IN_PROGRESS);

        CamelEntity camel1 = new CamelEntity();
        camel1.setColor(CamelEntity.CAMEL_COLOR.BLUE);
        camel1.setGame(game);
        camel1.setPosition(1);
        camel1.setStackPosition(0);
        game.getCamels().add(camel1);

        CamelEntity camel2 = new CamelEntity();
        camel2.setColor(CamelEntity.CAMEL_COLOR.GREEN);
        camel2.setGame(game);
        camel2.setPosition(1);
        camel2.setStackPosition(1);
        game.getCamels().add(camel2);

        return game;
    }

    /*
        Test camel moves over an oasis tile which pushes it 1 step forward.
     */
    @Test
    @Transactional
    void moveCamel_OasisBoost() {
        GameEntity game = createTestGameWithCamels();
        gameRepository.save(game);

        CamelEntity camel = game.getCamels().getFirst();
        camel.setPosition(3);

        PlayerEntity tileOwner = new PlayerEntity();
        tileOwner.setName("TestPlayer");
        tileOwner.setMoney(0);
        tileOwner.setGame(game);
        playerRepository.save(tileOwner);

        DesertTileEntity oasisTile = new DesertTileEntity();
        oasisTile.setGame(game);
        oasisTile.setOwner(tileOwner);
        oasisTile.setPosition(5);
        oasisTile.setType(DesertTileEntity.TileType.OASIS);
        game.getDesertTiles().add(oasisTile);

        desertTileRepository.save(oasisTile);

        camelMovementService.moveCamel(camel, 2);

        assertEquals(6, camel.getPosition(), "Camel should move to position 6 after hitting oasis");
    }

    /*
        Test camel moves over a mirage tile which pulls it 1 step backward.
     */
    @Test
    @Transactional
    void moveCamel_MiragePull() {
        GameEntity game = createTestGameWithCamels();
        gameRepository.save(game);

        CamelEntity camel = game.getCamels().getFirst();
        camel.setPosition(3);

        PlayerEntity tileOwner = new PlayerEntity();
        tileOwner.setName("TestPlayer");
        tileOwner.setMoney(0);
        tileOwner.setGame(game);
        playerRepository.save(tileOwner);

        DesertTileEntity mirageTile = new DesertTileEntity();
        mirageTile.setGame(game);
        mirageTile.setOwner(tileOwner);
        mirageTile.setPosition(5);
        mirageTile.setType(DesertTileEntity.TileType.MIRAGE);
        game.getDesertTiles().add(mirageTile);

        desertTileRepository.save(mirageTile);

        camelMovementService.moveCamel(camel, 2);

        assertEquals(4, camel.getPosition(), "Camel should move to position 4 after hitting mirage");
    }

    /*
        Test moving the entire camel stack forward maintains stack order.
        Initial:
        2: blue (1), orange (2), green (3)
        After move blue by 2:
        4: blue (1), orange (2), green (3)
     */
    @Test
    @Transactional
    void moveStack_AllMoveForward() {
        GameEntity game = new GameEntity();
        game.setRoomCode("STACK01");
        game.setState(GameEntity.GameState.IN_PROGRESS);

        CamelEntity blue = new CamelEntity(CamelEntity.CAMEL_COLOR.BLUE, game, 2, 1);
        CamelEntity orange = new CamelEntity(CamelEntity.CAMEL_COLOR.ORANGE, game, 2, 2);
        CamelEntity green = new CamelEntity(CamelEntity.CAMEL_COLOR.GREEN, game, 2, 3);

        game.getCamels().add(blue);
        game.getCamels().add(orange);
        game.getCamels().add(green);

        gameRepository.save(game);

        camelMovementService.moveCamel(blue, 2);

        assertEquals(4, blue.getPosition(), "Blue should move to 4");
        assertEquals(4, orange.getPosition(), "Orange should move to 4 with Blue");
        assertEquals(4, green.getPosition(), "Green should move to 4 with Blue");

        assertEquals(1, blue.getStackPosition(), "Blue should be bottom");
        assertEquals(2, orange.getStackPosition(), "Orange should be middle");
        assertEquals(3, green.getStackPosition(), "Green should be top");
    }

    /*
        Test moving a middle camel forward stacks it on existing camels properly.
        Initial:
        2: blue (1), orange (2), green (3)
        4: yellow (1)
        After move orange by 2:
        2: blue (1)
        4: yellow (1), orange (2), green (3)
     */
    @Test
    @Transactional
    void moveMiddleCamel_OnTopStack() {
        GameEntity game = new GameEntity();
        game.setRoomCode("STACK03");
        game.setState(GameEntity.GameState.IN_PROGRESS);

        CamelEntity blue = new CamelEntity(CamelEntity.CAMEL_COLOR.BLUE, game, 2, 1);
        CamelEntity orange = new CamelEntity(CamelEntity.CAMEL_COLOR.ORANGE, game, 2, 2);
        CamelEntity green = new CamelEntity(CamelEntity.CAMEL_COLOR.GREEN, game, 2, 3);
        CamelEntity yellow = new CamelEntity(CamelEntity.CAMEL_COLOR.YELLOW, game, 4, 1);

        game.getCamels().addAll(List.of(blue, orange, green, yellow));
        gameRepository.save(game);

        camelMovementService.moveCamel(orange, 2);

        assertEquals(2, blue.getPosition());
        assertEquals(4, orange.getPosition());
        assertEquals(4, green.getPosition());
        assertEquals(4, yellow.getPosition());

        assertEquals(1, yellow.getStackPosition(), "Yellow is base");
        assertEquals(2, orange.getStackPosition(), "Orange on top of yellow");
        assertEquals(3, green.getStackPosition(), "Green on top of orange");
    }

    /*
        Test a camel moves onto a stack from behind and becomes top.
        Initial:
        1: white (1)
        2: blue (1), orange (2), green (3)
        After move white by 1:
        2: blue (1), orange (2), green (3), white (4)
     */
    @Test
    @Transactional
    void moveCamel_JoinStackFromBehind() {
        GameEntity game = new GameEntity();
        game.setRoomCode("STACK04");
        game.setState(GameEntity.GameState.IN_PROGRESS);

        CamelEntity blue = new CamelEntity(CamelEntity.CAMEL_COLOR.BLUE, game, 2, 1);
        CamelEntity orange = new CamelEntity(CamelEntity.CAMEL_COLOR.ORANGE, game, 2, 2);
        CamelEntity green = new CamelEntity(CamelEntity.CAMEL_COLOR.GREEN, game, 2, 3);
        CamelEntity white = new CamelEntity(CamelEntity.CAMEL_COLOR.WHITE, game, 1, 1);

        game.getCamels().addAll(List.of(blue, orange, green, white));
        gameRepository.save(game);

        camelMovementService.moveCamel(white, 1);

        assertEquals(2, white.getPosition());
        assertEquals(4, white.getStackPosition());
    }

    /*
        Test stack moves onto oasis, the camel on oasis gets boosted forward.
        Initial:
        2: blue (1), orange (2), green (3)
        3: -
        4: oasis tile owned by TileOwner
        After move blue by 2:
        5: blue (1), orange (2), green (3)
     */
    @Test
    @Transactional
    void moveStack_OasisBoost() {
        GameEntity game = new GameEntity();
        game.setRoomCode("STACK05");
        game.setState(GameEntity.GameState.IN_PROGRESS);

        CamelEntity blue = new CamelEntity(CamelEntity.CAMEL_COLOR.BLUE, game, 2, 1);
        CamelEntity orange = new CamelEntity(CamelEntity.CAMEL_COLOR.ORANGE, game, 2, 2);
        CamelEntity green = new CamelEntity(CamelEntity.CAMEL_COLOR.GREEN, game, 2, 3);

        game.getCamels().addAll(List.of(blue, orange, green));

        PlayerEntity tileOwner = new PlayerEntity("TileOwner", 22);
        tileOwner.setGame(game);
        playerRepository.save(tileOwner);

        DesertTileEntity oasis = new DesertTileEntity();
        oasis.setGame(game);
        oasis.setOwner(tileOwner);
        oasis.setPosition(4);
        oasis.setType(DesertTileEntity.TileType.OASIS);
        desertTileRepository.save(oasis);

        game.getDesertTiles().add(oasis);
        gameRepository.save(game);

        camelMovementService.moveCamel(blue, 2);

        assertEquals(1, blue.getStackPosition());
        assertEquals(2, orange.getStackPosition());
        assertEquals(3, green.getStackPosition());

        assertEquals(5, blue.getPosition(), "Blue should move to 5 after oasis boost");
        assertEquals(5, orange.getPosition(), "Orange should follow blue to 5");
        assertEquals(5, green.getPosition(), "Green should follow orange to 5");
    }

    /*
        Test moving a partial stack onto mirage then stacking on another camel.
        Initial:
        1: no camels
        2: blue (1), orange (2), green (3)
        3: yellow (1)
        4: no camels (mirage here)

        After move orange by 2 (-1 due to mirage):

        1: no camels
        2: blue (1)
        3: orange (1), green (2), yellow (3)
        4: no camels (mirage here)
     */
    @Test
    @Transactional
    void movePartialStack_MirageThenStack() {
        GameEntity game = new GameEntity();
        game.setRoomCode("STACK06");
        game.setState(GameEntity.GameState.IN_PROGRESS);

        CamelEntity blue = new CamelEntity(CamelEntity.CAMEL_COLOR.BLUE, game, 2, 1);
        CamelEntity orange = new CamelEntity(CamelEntity.CAMEL_COLOR.ORANGE, game, 2, 2);
        CamelEntity green = new CamelEntity(CamelEntity.CAMEL_COLOR.GREEN, game, 2, 3);
        CamelEntity yellow = new CamelEntity(CamelEntity.CAMEL_COLOR.YELLOW, game, 3, 1);

        game.getCamels().addAll(List.of(blue, orange, green, yellow));

        PlayerEntity tileOwner = new PlayerEntity("TileOwner", 22);
        tileOwner.setGame(game);
        playerRepository.save(tileOwner);

        DesertTileEntity mirage = new DesertTileEntity();
        mirage.setGame(game);
        mirage.setOwner(tileOwner);
        mirage.setPosition(4);
        mirage.setType(DesertTileEntity.TileType.MIRAGE);
        desertTileRepository.save(mirage);

        game.getDesertTiles().add(mirage);
        gameRepository.save(game);

        camelMovementService.moveCamel(orange, 2);

        assertEquals(2, blue.getPosition(), "Blue should stay");
        assertEquals(3, orange.getPosition(), "Orange should land on Mirage at 4 and fall back to 3");
        assertEquals(3, green.getPosition(), "Green follows orange");
        assertEquals(3, yellow.getPosition(), "Yellow already at 3");

        // Stack order: yellow (1), orange (2), green (3)
        assertEquals(1, orange.getStackPosition());
        assertEquals(2, green.getStackPosition());
        assertEquals(3, yellow.getStackPosition());
    }

    /*
        Test camel passes finish line (>16) triggers victory.
     */
    @Test
    @Transactional
    void moveCamel_BeyondFinishLine_Wins() {
        GameEntity game = createTestGameWithCamels();
        gameRepository.save(game);

        CamelEntity camel = game.getCamels().getFirst();
        camel.setPosition(15);
        camel.setStackPosition(1);

        boolean isWinner = camelMovementService.moveCamel(camel, 2);

        assertTrue(isWinner, "Camel should be winner when passing position > 16");
        assertEquals(17, camel.getRawPosition(), "Camel rawPosition should be 17");
        assertEquals(1, camel.getPosition(), "Camel position should wrap to 1");
    }

    /*
        Test stack moves past finish line, only top camel declared winner.
        Initial:
        15: blue (1), green (2), orange (3)
        After move green by 2:
        15: blue (1)
        17: green (2), orange (3) - orange is now top and wins
     */
    @Test
    @Transactional
    void stackMoves_pastFinishLine_onlyTopCamelDeclaredWinner() {
        GameEntity game = new GameEntity();
        game.setRoomCode("WINSTACK");
        game.setState(GameEntity.GameState.IN_PROGRESS);

        CamelEntity blue = new CamelEntity(CamelEntity.CAMEL_COLOR.BLUE, game, 15, 1);
        CamelEntity green = new CamelEntity(CamelEntity.CAMEL_COLOR.GREEN, game, 15, 2);
        CamelEntity orange = new CamelEntity(CamelEntity.CAMEL_COLOR.ORANGE, game, 15, 3);

        game.getCamels().addAll(List.of(blue, green, orange));
        gameRepository.save(game);

        camelMovementService.moveCamel(green, 2);
        playerBetsService.raceBettingEval(game);

        List<CamelEntity> winners = game.getCamels().stream()
                .filter(CamelEntity::getRaceWinner)
                .toList();

        assertEquals(winners.size(), 1, "There is only one winner");
        assertEquals(winners.getFirst(), orange, "Top camel (orange) should trigger game win when passing finish line");

        assertEquals(15, blue.getRawPosition());
        assertEquals(17, green.getRawPosition());
        assertEquals(17, orange.getRawPosition());

        assertEquals(1, blue.getStackPosition());
        assertEquals(1, green.getStackPosition());
        assertEquals(2, orange.getStackPosition());

    }

    /*
        Test camel finishes exactly on position 16, should not win.
     */
    @Test
    @Transactional
    void camelFinishesExactlyOn16() {
        GameEntity game = createTestGameWithCamels();
        gameRepository.save(game);

        CamelEntity camel = game.getCamels().getFirst();
        camel.setPosition(14);

        boolean winner = camelMovementService.moveCamel(camel, 2); // 14 + 2 = 16

        assertFalse(winner, "Camel should not win if it lands exactly on position 16");
        assertEquals(16, camel.getPosition());
    }
}
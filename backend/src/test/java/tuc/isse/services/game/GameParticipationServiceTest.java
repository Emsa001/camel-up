package tuc.isse.services.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import tuc.isse.dto.GameDTO;
import tuc.isse.dto.PlayerDTO;
import tuc.isse.entities.GameEntity;
import tuc.isse.entities.PlayerEntity;
import tuc.isse.repositories.GameRepository;
import tuc.isse.repositories.PlayerRepository;
import tuc.isse.schemas.CreatePlayerRequest;
import tuc.isse.schemas.ErrorResponse;
import tuc.isse.services.player.PlayerTilesService;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameParticipationServiceTest {

    private GameParticipationService service;
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private GameSocketService gameSocketService;
    private PlayerTilesService playerTilesService;
    private GameRoundService gameRoundService;

    @BeforeEach
    void setUp() {
        gameRepository = mock(GameRepository.class);
        playerRepository = mock(PlayerRepository.class);
        gameSocketService = mock(GameSocketService.class);
        playerTilesService = mock(PlayerTilesService.class);
        gameRoundService = mock(GameRoundService.class);

        service = new GameParticipationService();
        service.gameRepository = gameRepository;
        service.playerRepository = playerRepository;
        service.gameSocketService = gameSocketService;
        service.playerTilesService = playerTilesService;
        service.gameRoundService = gameRoundService;
    }

    private GameEntity createTestGame(String roomCode, boolean isPrivate) {
        GameEntity game = new GameEntity();
        game.setRoomCode(roomCode);
        game.setPlayers(new ArrayList<>());
        game.setIsPrivate(isPrivate);
        game.setState(GameEntity.GameState.WAITING);
        return game;
    }

    @Test
    void joinGame_gameNotFound_returns404() {
        when(gameRepository.findByRoomCode("ABC")).thenReturn(Optional.empty());

        var result = service.joinGame("ABC", new CreatePlayerRequest());

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody() instanceof ErrorResponse);
    }

    @Test
    void joinGame_gameIsFull_returns400() {
        GameEntity game = createTestGame("FULL", false);
        for (int i = 0; i < GameEntity.MAX_PLAYERS; i++) {
            game.getPlayers().add(new PlayerEntity());
        }
        when(gameRepository.findByRoomCode("FULL")).thenReturn(Optional.of(game));

        var result = service.joinGame("FULL", new CreatePlayerRequest());

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Game is full", ((ErrorResponse) result.getBody()).getError());
    }

    @Test
    void joinGame_invalidPlayerName_returns400() {
        GameEntity game = createTestGame("BADNAME", false);
        when(gameRepository.findByRoomCode("BADNAME")).thenReturn(Optional.of(game));

        CreatePlayerRequest request = new CreatePlayerRequest();
        request.setName(""); // Invalid empty name

        var result = service.joinGame("BADNAME", request);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody() instanceof ErrorResponse);
    }

    @Test
    void joinGame_validRequest_returnsGameDTO() {
        GameEntity game = createTestGame("ROOM123", false);
        when(gameRepository.findByRoomCode("ROOM123")).thenReturn(Optional.of(game));

        CreatePlayerRequest request = new CreatePlayerRequest();
        request.setName("Player1");
        request.setAge(25);

        var result = service.joinGame("ROOM123", request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof GameDTO);

        verify(playerRepository).save(any(PlayerEntity.class));
        verify(gameSocketService).lobbyUpdate(game);
        verify(gameSocketService).roomUpdate(game);
    }

    @Test
    void leaveGame_gameNotFound_returns404() {
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setGameId(1L);

        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        var result = service.leaveGame(playerDTO);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Game not found", ((ErrorResponse) result.getBody()).getError());
    }

    @Test
    void leaveGame_playerNotInGame_returns400() {
        GameEntity game = new GameEntity();
        game.setId(1L);
        game.setPlayers(new ArrayList<>());

        PlayerDTO playerDTO = new PlayerDTO(999L, "Ghost", 30, 0, null, 1L);
        PlayerEntity ghost = new PlayerEntity();
        ghost.setId(999L);

        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(playerRepository.findById(999L)).thenReturn(Optional.of(ghost));

        var result = service.leaveGame(playerDTO);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Player not in game", ((ErrorResponse) result.getBody()).getError());
    }

    @Test
    void leaveGame_onlyPlayer_gameDeleted() {
        GameEntity game = new GameEntity();
        game.setId(1L);
        game.setRoomCode("X");

        PlayerEntity player = new PlayerEntity();
        player.setId(1L);
        player.setName("Solo");
        player.setGame(game);

        game.setPlayers(new ArrayList<>());
        game.getPlayers().add(player);

        PlayerDTO playerDTO = new PlayerDTO(1L, "Solo", 25, 0, null, 1L);

        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        var result = service.leaveGame(playerDTO);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(gameRepository).delete(game);
        assertEquals("Game deleted as it is empty", ((ErrorResponse) result.getBody()).getError());
    }

    @Test
    void leaveGame_multiplePlayers_removesPlayer() {
        GameEntity game = new GameEntity();
        game.setId(1L);
        game.setRoomCode("X");

        PlayerEntity player1 = new PlayerEntity();
        player1.setId(1L);
        player1.setName("Host");

        PlayerEntity player2 = new PlayerEntity();
        player2.setId(2L);
        player2.setName("Player");

        player1.setGame(game);
        player2.setGame(game);

        game.setPlayers(new ArrayList<>());
        game.getPlayers().add(player1);
        game.getPlayers().add(player2);
        game.setHost(player1);

        PlayerDTO leavingPlayer = new PlayerDTO(1L, "Host", 25, 0, null, 1L);

        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player1));

        var result = service.leaveGame(leavingPlayer);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(gameSocketService).roomUpdate(game);
        verify(gameSocketService).lobbyUpdate(game);
        verify(playerTilesService).deletePlayerTiles(player1);
        assertNull(player1.getGame());
        assertEquals(1, game.getPlayers().size());
        assertTrue(result.getBody() instanceof GameDTO);
    }
}
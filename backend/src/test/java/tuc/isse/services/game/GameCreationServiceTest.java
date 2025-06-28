package tuc.isse.services.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import tuc.isse.entities.GameEntity;
import tuc.isse.repositories.GameRepository;
import tuc.isse.schemas.CreateGameRequest;
import tuc.isse.schemas.CreatePlayerRequest;
import tuc.isse.schemas.ErrorResponse;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameCreationServiceTest {

    private GameRepository gameRepository;
    private GameSocketService gameSocketService;
    private GameCreationService gameCreationService;

    @BeforeEach
    public void setup() {
        gameRepository = mock(GameRepository.class);
        gameSocketService = mock(GameSocketService.class);
        gameCreationService = new GameCreationService();
        gameCreationService.gameRepository = gameRepository;
        gameCreationService.gameSocketService = gameSocketService;
    }

    @Test
    public void testCreateGame_Successful() {
        CreatePlayerRequest host = new CreatePlayerRequest();
        host.setName("Alice");
        host.setAge(30);

        CreateGameRequest request = new CreateGameRequest();
        request.setName("TestGame");
        request.setIsPrivate(false);
        request.setHost(host);

        when(gameRepository.findAll()).thenReturn(Collections.emptyList());
        when(gameRepository.findByRoomCode(anyString())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = gameCreationService.createGame(request);

        assertEquals(201, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof GameEntity);

        verify(gameSocketService).lobbyUpdate(any(GameEntity.class));
        verify(gameRepository).save(any(GameEntity.class));
    }

    @Test
    public void testCreateGame_TooManyPublicGames() {
        CreateGameRequest request = new CreateGameRequest();
        request.setIsPrivate(false);

        var maxGames = Collections.nCopies(GameEntity.MAX_PUBLIC_GAMES, new GameEntity());
        when(gameRepository.findAll()).thenReturn(maxGames);

        ResponseEntity<Object> response = gameCreationService.createGame(request);

        assertEquals(409, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }

    @Test
    public void testCreateGame_RoomCodeConflict() {
        CreatePlayerRequest host = new CreatePlayerRequest();
        host.setName("Bob");
        host.setAge(22);

        CreateGameRequest request = new CreateGameRequest();
        request.setIsPrivate(true);
        request.setName("PrivateGame");
        request.setHost(host);

        when(gameRepository.findAll()).thenReturn(Collections.emptyList());
        when(gameRepository.findByRoomCode(anyString())).thenReturn(Optional.of(new GameEntity()));

        ResponseEntity<Object> response = gameCreationService.createGame(request);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }
}
package tuc.isse.services.game;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tuc.isse.entities.GameEntity;
import tuc.isse.entities.PlayerEntity;
import tuc.isse.models.ChatModel;
import tuc.isse.repositories.GameRepository;
import tuc.isse.repositories.PlayerRepository;

/**
 * Service for handling chat messages in a game room.
 * It processes incoming chat messages, validates the game room and player,
 * and sends the message to the appropriate channel.
 */
@Service
public class GameChatService {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    GameSocketService gameSocketService;

    /**
     * Processes a chat message in a game room.
     * Validates the game room and player, then sends the message to the appropriate channel.
     *
     * @param roomCode the unique identifier for the game room
     * @param message  the chat message to be processed
     */
    @Transactional
    public void chatMessage(String roomCode, ChatModel message) {
        // 1. Validate the game room code
        GameEntity game = gameRepository.findByRoomCode(roomCode).orElse(null);
        if (game == null)
            return ;

        // 2. Validate the player and check if they are part of the game
        PlayerEntity player = playerRepository.findByName(message.getPlayerName()).orElse(null);
        if (player == null || !game.getPlayers().contains(player))
            return;

        // 3. Send the chat message to the game room
        gameSocketService.chatMessage(game, message);
    }

}

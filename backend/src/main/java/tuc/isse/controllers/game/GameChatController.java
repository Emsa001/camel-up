package tuc.isse.controllers.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import tuc.isse.models.ChatModel;
import tuc.isse.services.game.GameChatService;

/**
 * Controller for handling chat messages in a game room.
 * It listens for messages sent to the specified room and processes them.
 */
@Controller
public class GameChatController extends GameController {

    @Autowired
    GameChatService gameChatService;

    /**
     * Handles incoming chat messages for a specific game room.
     *
     * @param roomCode the unique identifier for the game room
     * @param message  the chat message to be processed
     */
    @MessageMapping("{roomCode}/chat")
    public void receiveChatMessage(@DestinationVariable String roomCode, ChatModel message) {
        gameChatService.chatMessage(
                roomCode,
                message
        );
    }
}

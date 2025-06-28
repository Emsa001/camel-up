package tuc.isse.services.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tuc.isse.dto.GameDTO;
import tuc.isse.entities.GameEntity;
import tuc.isse.models.ChatModel;

import java.util.HashMap;
import java.util.Map;

@Service
public class GameSocketService {
    @Autowired
    SimpMessagingTemplate messagingTemplate;

    public void lobbyUpdate(GameEntity game) {
        if (game.getIsPrivate()) return;
        messagingTemplate.convertAndSend("/topic/games", new GameDTO(game));
    }

    public void roomUpdate(GameEntity game) {
        messagingTemplate.convertAndSend("/topic/game/" + game.getRoomCode(), new GameDTO(game));
    }

    public void chatMessage(GameEntity game, ChatModel message){
        messagingTemplate.convertAndSend("/topic/game/" + game.getRoomCode() + "/chat", message);
    }

    public void action(GameEntity game, String action, Object payload) {
        messagingTemplate.convertAndSend("/topic/game/" + game.getRoomCode() + "/" + action, payload);
    }

    public void turnUpdate(GameEntity game) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("round", game.getRound());
        payload.put("currentPlayerId", game.getCurrentPlayer().getId());
        payload.put("turn", game.getTurn());
        payload.put("status", game.getState());

        messagingTemplate.convertAndSend("/topic/game/" + game.getRoomCode(), payload);    }
}

package tuc.isse.controllers.socket;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import tuc.isse.dto.PlayerDTO;
import tuc.isse.entities.GameEntity;
import tuc.isse.entities.PlayerEntity;
import tuc.isse.services.game.GameParticipationService;

@Component
public class SocketDisconnect extends SocketController {

    @Autowired
    GameParticipationService gameParticipationService;

    /**
     * Handles the disconnection of a WebSocket session.
     * When a session disconnects, it removes the player from the sessionPlayerMap,
     * checks if the player is still in a game, and if not, deletes the player.
     * If the player is in a game, it calls the leaveGame method to handle the disconnection.
     *
     * @param event The SessionDisconnectEvent containing information about the disconnected session.
     */
    @Transactional
    @EventListener
    public void disconnectionListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        PlayerEntity sessionPlayer = sessionPlayerMap.remove(sessionId);
        if (sessionPlayer == null) return;

        PlayerEntity player = playerRepository.findById(sessionPlayer.getId()).orElse(null);
        if (player == null) return;

        if (player.getGame() == null) {
            playerRepository.delete(player);
            return;
        }

        GameEntity game = gameRepository.findById(player.getGame().getId()).orElse(null);
        if (game == null) return;

        gameParticipationService.leaveGame(new PlayerDTO(player));
    }
}

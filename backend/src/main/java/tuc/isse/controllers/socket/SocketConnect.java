package tuc.isse.controllers.socket;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import tuc.isse.entities.PlayerEntity;
import tuc.isse.utils.Logger;

@Component
public class SocketConnect extends SocketController {

    /**
     * Handles the connection event for WebSocket sessions.
     * It retrieves the user ID from the session headers and maps it to the player entity.
     *
     * @param event The session connect event containing the message with headers.
     */
    @EventListener
    public void connectionListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String userId = headerAccessor.getNativeHeader("id") != null 
            ? headerAccessor.getNativeHeader("id").get(0) 
            : null;

        if (userId == null) return;

        PlayerEntity player = playerRepository.findById(Long.parseLong(userId)).orElse(null);
        if (player == null) {
            Logger.error("Player not found: " + userId);
            return;
        }

        String sessionId = headerAccessor.getSessionId();
        SocketController.sessionPlayerMap.put(sessionId, player);

        Logger.info("Player connected: " + userId + " (Session: " + sessionId + ")");
    }

}
package com.pokemon.tcg.modules.game.events;

import com.pokemon.tcg.modules.game.services.GameConnectionService;
import com.pokemon.tcg.modules.game.services.GameDisconnectService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class EnterSuscriptionListener {
    private static final String TARGET_DESTINATION = "/topic/game/";
    private final GameConnectionService gameConnectionService;
    private final GameDisconnectService gameDisconnectService;

    @EventListener
    public void handleSubscriptionEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        Principal principal = accessor.getUser();

        if (destination != null && destination.startsWith(TARGET_DESTINATION)) {
            String gameId = destination.replace(TARGET_DESTINATION,"");

            if (principal != null) {
                try {
                    gameConnectionService.connectToGame(gameId, principal);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    @EventListener
    public void handleDisconnectionEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        Principal principal = accessor.getUser();

        if (destination != null && destination.startsWith(TARGET_DESTINATION)) {
            String gameId = destination.replace(TARGET_DESTINATION,"");

            if (principal != null) {
                gameDisconnectService.disconnectToGame(gameId,principal);
            }
        }
    }
}

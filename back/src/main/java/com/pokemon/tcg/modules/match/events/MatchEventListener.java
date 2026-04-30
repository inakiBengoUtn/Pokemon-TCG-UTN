package com.pokemon.tcg.modules.match.events;

import com.pokemon.tcg.modules.match.Exceptions.UnauthenticatedUserException;
import com.pokemon.tcg.modules.match.services.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@RequiredArgsConstructor
public class MatchEventListener {

    private static final String TARGET_DESTINATION = "/user/queue/match";
    private final MatchService matchService;

    // Se ejeuta cuando un usuario suscribe
    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor header = StompHeaderAccessor.wrap(event.getMessage());
        String destination = header.getDestination();

        if (destination == null) return;
        if (destination.startsWith(TARGET_DESTINATION)) {
            if(event.getUser() == null) {
                throw new UnauthenticatedUserException();
            }

            this.matchService.addToQueue(event.getUser().getName());
            this.matchService.findGame();
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        if(event.getUser() == null) {
            throw new UnauthenticatedUserException();
        }
        this.matchService.removeUser(event.getUser().getName());
    }
}

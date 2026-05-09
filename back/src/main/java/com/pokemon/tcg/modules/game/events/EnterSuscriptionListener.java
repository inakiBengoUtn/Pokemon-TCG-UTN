package com.pokemon.tcg.modules.game.events;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;

@Component
public class EnterSuscriptionListener {
    private static final String TARGET_DESTINATION = "/topic/game/";

    @EventListener
    public void handleSubscriptionEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accesor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accesor.getDestination();
        Principal principal = accesor.getUser();

        if (destination != null && destination.startsWith(TARGET_DESTINATION)) {
            String gameId = destination.replace(TARGET_DESTINATION,"");
//
//            if (principal != null) {
//                try {
//
//                } catch (ex) {
//                    // desconectar usuario de esta suscripcion
//                }
//            }
        }
    }
}

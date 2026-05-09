package com.pokemon.tcg.modules.game.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class GameConnectionService {
    private final StringRedisTemplate redisTemplate;

    public void connectToGame(String gameId, Principal principal) {
        String redisKey = "game:" + gameId;
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();

        Map<String, String> gameData = hashOps.entries(redisKey);

        if (gameData.isEmpty()) {
            throw new IllegalArgumentException("The game "+gameId+" no exists.");
        }

        String username = principal.getName();
        String player1 = gameData.get("player1");
        String player2 = gameData.get("player2");

        if (!username.equals(player1) && !username.equals(player2)) {
            throw new SecurityException("Access denied: you are not a player in this game.");
        }

        String readyField = username.equals(player1) ?"player1_ready" :"player2_ready";
        hashOps.put(redisKey,readyField,"true");

        gameData = hashOps.entries(redisKey);
        boolean p1Ready = "true".equals(gameData.get("player1_ready"));
        boolean p2Ready = "true".equals(gameData.get("player2_ready"));

        if (p1Ready && p2Ready && "WAITING_FOR_READY".equals(gameData.get("status"))) {
            hashOps.put(redisKey, "status", "SETUP_PHASE");

            initializeGameState(gameId, player1, player2);
        }
    }

    private void initializeGameState(String gameId, String player1, String player2) {
//        GameState initialState = stateMachineFactory.createNewGame(player1, player2);

        // Guardamos el objeto anidado completo como JSON en una nueva key de Redis
//        redisTemplate.opsForValue().set("gamestate:" + gameId, initialState);
//        redisTemplate.opsForHash().delete("game:"+gameId);

        // Opcional: Emitir un evento (ej. vía WebSocket) a ambos jugadores
        // indicando que el "SETUP_PHASE" ha comenzado y enviarles sus manos iniciales.
    }
}

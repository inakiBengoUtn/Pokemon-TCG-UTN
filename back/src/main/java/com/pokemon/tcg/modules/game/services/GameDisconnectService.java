package com.pokemon.tcg.modules.game.services;

import com.pokemon.tcg.modules.game.exceptions.GameNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GameDisconnectService {
    private final StringRedisTemplate redisTemplate;

    public void disconnectToGame(String gameId, Principal principal) {
        String redisKey = "game:"+gameId;
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();

        Map<String, String> gameData = hashOps.entries(redisKey);

        if (gameData.isEmpty()) {
            throw new GameNotFoundException(gameId);
        }

        String username = principal.getName();
        String player1 = gameData.get("player1");
        String player2 = gameData.get("player2");

        if (!username.equals(player1) && !username.equals(player2)) {
            throw new SecurityException("Access denied: you are not a player in this game.");
        }

    }
}

package com.pokemon.tcg.modules.match.services;

import com.pokemon.tcg.modules.match.dto.response.MatchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class MatchService {
    private final StringRedisTemplate redisTemplate;
    private static final String QUEUE_KEY = "matchmaking:queue";
    private final SimpMessagingTemplate messagingTemplate;

    public void addToQueue(String username) {
        double score = System.currentTimeMillis();

        redisTemplate.opsForZSet().add(QUEUE_KEY, username, score);
        log.info("Registered user "+username+" in match queue.");
    }

    public void findGame() {
        Set<String> players = redisTemplate.opsForZSet().range(QUEUE_KEY,0,1);

        if (players != null && players.size() == 2) {
            String[] matchedPlayers = players.toArray(new String[0]);
            Long removed = redisTemplate.opsForZSet().remove(QUEUE_KEY, (Object[]) matchedPlayers);

            if (removed != null && removed == 2) {
                startNewGame(matchedPlayers[0], matchedPlayers[1]);
            }
        }
    }

    private void startNewGame(String player1, String player2) {
        String gameKey = "game:" + UUID.randomUUID().toString();
        log.info("Game "+gameKey+" created");

        Map<String, String> gameData = new HashMap<>();
        gameData.put("player1", player1);
        gameData.put("player2", player2);
        gameData.put("status","WAITING_FOR_READY");
        gameData.put("createdAt",String.valueOf(System.currentTimeMillis()));

        redisTemplate.opsForHash().putAll(gameKey, gameData);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(gameKey);

        messagingTemplate.convertAndSendToUser(player1, "/queue/match", matchResponse);
        messagingTemplate.convertAndSendToUser(player2, "/queue/match", matchResponse);
    }

    public void removeUser(String username) {
        this.redisTemplate.opsForZSet().remove(QUEUE_KEY,username);
    }
}

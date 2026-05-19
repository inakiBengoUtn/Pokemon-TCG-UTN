package com.pokemon.tcg.modules.game.statemachine;

import com.pokemon.tcg.modules.game.domain.GamePhase;
import com.pokemon.tcg.modules.game.domain.state.GameState;
import com.pokemon.tcg.modules.game.domain.state.Player;
import com.pokemon.tcg.modules.game.dto.responses.CoinFlipResultPayload;
import com.pokemon.tcg.modules.game.dto.responses.GameEventResponse;
import com.pokemon.tcg.modules.game.dto.responses.GameEventTypes;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;

@RequiredArgsConstructor
public class CreateStateGame {
    private final SimpMessagingTemplate messagingTemplate;

    public void create(String player1, String player2) {
        String gameId = UUID.randomUUID().toString();
        CoinFlipResultPayload coinFlipResultPayload;

        if (flipCoin()) {
            coinFlipResultPayload = new CoinFlipResultPayload("heads",player1);
        } else {
            coinFlipResultPayload = new CoinFlipResultPayload("tails",player2);
        };

        GameState.builder()
                .gameId(gameId)
                .turnCount(0)
                .activePlayerId(coinFlipResultPayload.getStartingPlayerId())
                .currentPhase(GamePhase.STARTING)
                .player1(createPlayer(player1))
                .player2(createPlayer(player2))
                .build();

        GameEventResponse<CoinFlipResultPayload> response = new GameEventResponse<CoinFlipResultPayload>();
        response.setType(GameEventTypes.COIN_FLIP_RESULT);
        response.setGameId(gameId);
        response.setPayload(coinFlipResultPayload);
        messagingTemplate.convertAndSend("/topic/game/"+gameId, response);
    }

    // esto debe estar en game engine
    private boolean flipCoin() {
        int randomInt = (int) (Math.random() * 2);
        return randomInt == 1;
    }

    private Player createPlayer(String playerName) {
        return Player.builder().name(playerName).build();
    }
}

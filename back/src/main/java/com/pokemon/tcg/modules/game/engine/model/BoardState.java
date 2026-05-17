package com.pokemon.tcg.modules.game.engine.model;

import com.pokemon.tcg.common.enums.TurnPhase;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardState {

    private String matchId;
    private String player1Id;
    private String player2Id;

    private PlayerBoard player1Board;
    private PlayerBoard player2Board;

    private String currentTurnPlayerId;

    // Turn 1 = first turn ever (player1 can't attack on turn 1 in standard rules,
    // but we allow it for XY Unlimited per the rulebook flow)
    private int turnNumber;

    @Builder.Default
    private String turnPhase = TurnPhase.DRAW.name();

    // Player who takes the first turn (decided by coin flip during setup)
    private String firstTurnPlayerId;

    // Accumulated card data — populated once at game start so the engine
    // never needs to re-query the DB for card details mid-game
    @Builder.Default
    private Map<String, CardSnapshot> cardCache = new HashMap<>();

    private boolean suddenDeath;

    // Non-null when a winner has been decided
    private String winnerId;

    // ---- helpers ----

    public PlayerBoard getBoardFor(String playerId) {
        return playerId.equals(player1Id) ? player1Board : player2Board;
    }

    public PlayerBoard getOpponentBoard(String playerId) {
        return playerId.equals(player1Id) ? player2Board : player1Board;
    }

    public String getOpponentId(String playerId) {
        return playerId.equals(player1Id) ? player2Id : player1Id;
    }

    public boolean isGameOver() {
        return winnerId != null;
    }
}

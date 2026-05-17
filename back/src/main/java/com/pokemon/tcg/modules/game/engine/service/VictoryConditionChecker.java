package com.pokemon.tcg.modules.game.engine.service;

import com.pokemon.tcg.modules.game.engine.model.BoardState;
import com.pokemon.tcg.modules.game.engine.model.PlayerBoard;
import org.springframework.stereotype.Service;

/**
 * Checks the three victory conditions defined by the XY rulebook:
 * <ol>
 *   <li>A player takes their last prize card.</li>
 *   <li>A player has no Pokémon left in play (active + bench all KO'd).</li>
 *   <li>A player cannot draw a card at the start of their turn (deck is empty).</li>
 * </ol>
 * Also handles the Sudden Death trigger when both players satisfy condition 1 simultaneously.
 */
@Service
public class VictoryConditionChecker {

    /**
     * Check all conditions. Updates {@code state.winnerId} if the game is over.
     * Returns the winner's playerId, or null if the game continues.
     */
    public String checkAfterAction(BoardState state) {
        if (state.isGameOver()) return state.getWinnerId();

        PlayerBoard p1 = state.getPlayer1Board();
        PlayerBoard p2 = state.getPlayer2Board();

        boolean p1WinsPrizes = p1.prizesRemaining() == 0;
        boolean p2WinsPrizes = p2.prizesRemaining() == 0;

        // Sudden Death: both took their last prize simultaneously
        if (p1WinsPrizes && p2WinsPrizes) {
            state.setSuddenDeath(true);
            // Game continues with Sudden Death rules — no winner yet
            return null;
        }

        if (p1WinsPrizes) return declareWinner(state, state.getPlayer1Id());
        if (p2WinsPrizes) return declareWinner(state, state.getPlayer2Id());

        // Condition 2: no Pokémon in play
        if (p2.hasNoPokemon()) return declareWinner(state, state.getPlayer1Id());
        if (p1.hasNoPokemon()) return declareWinner(state, state.getPlayer2Id());

        return null;
    }

    /**
     * Condition 3: checked at the start of a player's DRAW phase.
     * If the player cannot draw (empty deck), the opponent wins.
     */
    public String checkCannotDraw(BoardState state, String drawingPlayerId) {
        if (state.isGameOver()) return state.getWinnerId();
        PlayerBoard board = state.getBoardFor(drawingPlayerId);
        if (board.getDeck().isEmpty()) {
            return declareWinner(state, state.getOpponentId(drawingPlayerId));
        }
        return null;
    }

    private String declareWinner(BoardState state, String winnerId) {
        state.setWinnerId(winnerId);
        return winnerId;
    }
}

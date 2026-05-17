package com.pokemon.tcg.modules.game.engine.service;

import com.pokemon.tcg.common.enums.TurnPhase;
import com.pokemon.tcg.modules.game.engine.model.BoardState;
import com.pokemon.tcg.modules.game.engine.model.CardInstance;
import com.pokemon.tcg.modules.game.engine.model.PlayerBoard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * State machine for the turn cycle: DRAW → MAIN → ATTACK → BETWEEN_TURNS.
 *
 * Responsibilities:
 * <ul>
 *   <li>Begin a new turn: draw a card, advance phase to MAIN.</li>
 *   <li>Transition between phases (MAIN → ATTACK, ATTACK → BETWEEN_TURNS → next DRAW).</li>
 *   <li>Apply between-turns status effects via {@link StatusEffectManager}.</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TurnManager {

    private final StatusEffectManager statusEffectManager;
    private final VictoryConditionChecker victoryChecker;

    /**
     * Start a new turn for the current player:
     * 1. Check draw condition (cannot draw → opponent wins).
     * 2. Draw 1 card.
     * 3. Set phase to MAIN.
     * 4. Reset per-turn flags.
     *
     * @return drawn CardInstance, or null if deck is empty (winner already set).
     */
    public CardInstance beginTurn(BoardState state) {
        String pid = state.getCurrentTurnPlayerId();
        PlayerBoard board = state.getBoardFor(pid);

        // Victory condition 3: cannot draw
        String winner = victoryChecker.checkCannotDraw(state, pid);
        if (winner != null) {
            log.info("Game over — {} cannot draw, {} wins", pid, winner);
            return null;
        }

        CardInstance drawn = board.drawCard();
        board.resetTurnFlags();
        state.setTurnPhase(TurnPhase.MAIN.name());

        log.debug("Turn {} started for {} — drew {}", state.getTurnNumber(), pid,
                drawn != null ? drawn.getCardId() : "nothing");
        return drawn;
    }

    /**
     * Transition from MAIN to ATTACK phase.
     * Called explicitly by the player (or auto-triggered when they declare an attack).
     */
    public void enterAttackPhase(BoardState state) {
        state.setTurnPhase(TurnPhase.ATTACK.name());
    }

    /**
     * End the current player's turn:
     * 1. Apply between-turns status effects on the ending player's active.
     * 2. Check victory conditions.
     * 3. Swap current player.
     * 4. Increment turn counter if player2 just finished.
     * 5. Set phase to DRAW (ready for the next beginTurn call).
     *
     * @return winner's playerId if the game ended, null otherwise.
     */
    public String endTurn(BoardState state) {
        String pid = state.getCurrentTurnPlayerId();
        PlayerBoard board = state.getBoardFor(pid);

        // Between-turns effects on the active Pokémon of the ending player
        state.setTurnPhase(TurnPhase.BETWEEN_TURNS.name());
        statusEffectManager.applyBetweenTurns(board);

        // Check KO'd pokemon (status damage may have knocked them out)
        String winner = victoryChecker.checkAfterAction(state);
        if (winner != null) {
            log.info("Game over after between-turns — winner: {}", winner);
            return winner;
        }

        // Swap turn
        String nextPid = state.getOpponentId(pid);
        state.setCurrentTurnPlayerId(nextPid);

        // Increment turn number after player2 finishes (both players done = 1 round)
        if (nextPid.equals(state.getPlayer1Id())) {
            state.setTurnNumber(state.getTurnNumber() + 1);
        }

        state.setTurnPhase(TurnPhase.DRAW.name());
        log.debug("Turn ended for {}. Next: {} (turn {})", pid, nextPid, state.getTurnNumber());
        return null;
    }
}

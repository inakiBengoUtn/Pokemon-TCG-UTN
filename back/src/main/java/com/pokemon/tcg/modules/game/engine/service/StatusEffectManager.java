package com.pokemon.tcg.modules.game.engine.service;

import com.pokemon.tcg.modules.game.engine.model.PlayerBoard;
import com.pokemon.tcg.modules.game.engine.model.PokemonInPlay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Resolves between-turns special-condition effects in the order defined by the XY rulebook:
 * Poisoned → Burned → Asleep → Paralyzed
 *
 * Called at the start of BETWEEN_TURNS phase before the next player's DRAW phase.
 */
@Slf4j
@Service
public class StatusEffectManager {

    private final Random rng;

    public StatusEffectManager() { this.rng = new Random(); }
    public StatusEffectManager(Random rng) { this.rng = rng; }

    /**
     * Apply between-turns effects to the active Pokémon of the player whose turn just ended.
     * Modifies the PokemonInPlay in place.
     *
     * @param board the board of the player who just finished their turn
     */
    public void applyBetweenTurns(PlayerBoard board) {
        PokemonInPlay active = board.getActivePokemon();
        if (active == null) return;

        // 1. Poisoned — 1 damage counter (10 HP) per between-turns
        if (active.isPoisoned()) {
            active.setCurrentHp(Math.max(0, active.getCurrentHp() - 10));
            log.debug("Poison: {} takes 10 damage (HP now {})", active.getCardId(), active.getCurrentHp());
        }

        // 2. Burned — flip coin; tails = 2 damage counters (20 HP), heads = remove burn
        if (active.isBurned()) {
            boolean headsRemovesBurn = rng.nextBoolean();
            if (headsRemovesBurn) {
                active.setBurned(false);
                log.debug("Burn: {} — heads, burn removed", active.getCardId());
            } else {
                active.setCurrentHp(Math.max(0, active.getCurrentHp() - 20));
                log.debug("Burn: {} — tails, 20 damage (HP now {})", active.getCardId(), active.getCurrentHp());
            }
        }

        // 3. Asleep — flip coin; heads = wake up, tails = stay asleep
        if (active.isAsleep()) {
            boolean headsWakeUp = rng.nextBoolean();
            if (headsWakeUp) {
                active.setAsleep(false);
                log.debug("Sleep: {} woke up", active.getCardId());
            } else {
                log.debug("Sleep: {} stays asleep", active.getCardId());
            }
        }

        // 4. Paralyzed — automatically removed at end of the paralyzed player's turn
        if (active.isParalyzed()) {
            active.setParalyzed(false);
            log.debug("Paralysis: {} recovered from paralysis", active.getCardId());
        }
    }
}

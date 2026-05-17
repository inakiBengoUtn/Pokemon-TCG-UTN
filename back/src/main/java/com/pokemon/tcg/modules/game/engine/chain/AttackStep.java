package com.pokemon.tcg.modules.game.engine.chain;

/**
 * One step in the 7-step attack pipeline (Chain of Responsibility).
 */
public interface AttackStep {

    /**
     * Execute this step, then hand off to the next step if present.
     * Implementations should check {@link AttackContext#isCancelled()} before doing work.
     */
    void execute(AttackContext ctx, AttackStep next);
}

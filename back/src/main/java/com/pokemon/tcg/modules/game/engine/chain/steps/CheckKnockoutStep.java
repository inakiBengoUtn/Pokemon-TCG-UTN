package com.pokemon.tcg.modules.game.engine.chain.steps;

import com.pokemon.tcg.modules.game.engine.chain.AttackContext;
import com.pokemon.tcg.modules.game.engine.chain.AttackStep;

/**
 * Step 7 (final): Mark the defender as knocked out if its HP reached 0.
 */
public class CheckKnockoutStep implements AttackStep {

    @Override
    public void execute(AttackContext ctx, AttackStep next) {
        if (ctx.isCancelled()) return; // last step — no next

        if (ctx.getDefender().isKnockedOut()) {
            ctx.setDefenderKnockedOut(true);
        }
        // next is intentionally not called — this is the end of the chain
    }
}

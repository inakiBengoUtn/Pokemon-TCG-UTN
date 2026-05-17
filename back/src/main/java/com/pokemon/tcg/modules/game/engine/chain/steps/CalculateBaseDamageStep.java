package com.pokemon.tcg.modules.game.engine.chain.steps;

import com.pokemon.tcg.modules.game.engine.chain.AttackContext;
import com.pokemon.tcg.modules.game.engine.chain.AttackStep;

/**
 * Step 3: Parse the attack's damage string into a numeric base damage.
 * Handles "60", "20+", "60×", "" (no damage).
 * Variable damage ("+" or "×") defaults to the printed number as base;
 * actual modifiers from attack text are not fully simulated here.
 */
public class CalculateBaseDamageStep implements AttackStep {

    @Override
    public void execute(AttackContext ctx, AttackStep next) {
        if (ctx.isCancelled()) { if (next != null) next.execute(ctx, null); return; }

        String dmgStr = ctx.getAttack().getDamage();
        int base = 0;

        if (dmgStr != null && !dmgStr.isBlank()) {
            // Strip trailing "+" or "×" modifiers and parse the numeric part
            String numeric = dmgStr.replaceAll("[^0-9]", "");
            if (!numeric.isEmpty()) {
                base = Integer.parseInt(numeric);
            }
        }

        ctx.setBaseDamage(base);
        ctx.setModifiedDamage(base);

        if (next != null) next.execute(ctx, null);
    }
}

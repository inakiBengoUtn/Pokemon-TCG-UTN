package com.pokemon.tcg.modules.game.engine.chain.steps;

import com.pokemon.tcg.modules.game.engine.chain.AttackContext;
import com.pokemon.tcg.modules.game.engine.chain.AttackStep;
import com.pokemon.tcg.modules.game.engine.model.PokemonInPlay;

/**
 * Step 6: Subtract the final modified damage from the defender's current HP.
 */
public class ApplyDamageStep implements AttackStep {

    @Override
    public void execute(AttackContext ctx, AttackStep next) {
        if (ctx.isCancelled()) { if (next != null) next.execute(ctx, null); return; }

        PokemonInPlay defender = ctx.getDefender();
        int dmg = ctx.getModifiedDamage();

        if (dmg > 0) {
            defender.setCurrentHp(Math.max(0, defender.getCurrentHp() - dmg));
        }

        if (next != null) next.execute(ctx, null);
    }
}

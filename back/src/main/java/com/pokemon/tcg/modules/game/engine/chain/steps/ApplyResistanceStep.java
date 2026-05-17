package com.pokemon.tcg.modules.game.engine.chain.steps;

import com.pokemon.tcg.modules.game.engine.chain.AttackContext;
import com.pokemon.tcg.modules.game.engine.chain.AttackStep;
import com.pokemon.tcg.modules.game.engine.model.CardSnapshot;

import java.util.List;

/**
 * Step 5: Apply resistance.
 * If the defending Pokémon resists any of the attacker's types, subtract 20 (minimum 0).
 */
public class ApplyResistanceStep implements AttackStep {

    @Override
    public void execute(AttackContext ctx, AttackStep next) {
        if (ctx.isCancelled()) { if (next != null) next.execute(ctx, null); return; }

        CardSnapshot defenderCard = ctx.getBoardState().getCardCache()
                .get(ctx.getDefender().getCardId());
        CardSnapshot attackerCard = ctx.getBoardState().getCardCache()
                .get(ctx.getAttacker().getCardId());

        if (defenderCard != null && defenderCard.getResistanceType() != null
                && attackerCard != null && attackerCard.getTypes() != null) {

            List<String> attackerTypes = attackerCard.getTypes();
            String resistType = defenderCard.getResistanceType();

            if (attackerTypes.contains(resistType)) {
                // XY format: -20
                int reduced = ctx.getModifiedDamage() - 20;
                ctx.setModifiedDamage(Math.max(0, reduced));
            }
        }

        if (next != null) next.execute(ctx, null);
    }
}

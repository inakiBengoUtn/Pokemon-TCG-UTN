package com.pokemon.tcg.modules.game.engine.chain.steps;

import com.pokemon.tcg.modules.game.engine.chain.AttackContext;
import com.pokemon.tcg.modules.game.engine.chain.AttackStep;
import com.pokemon.tcg.modules.game.engine.model.CardSnapshot;

import java.util.List;

/**
 * Step 4: Apply weakness.
 * If the defending Pokémon is weak to any of the attacker's types, multiply damage by 2.
 * XY rulebook: weakness is ×2 applied to the modified damage so far.
 */
public class ApplyWeaknessStep implements AttackStep {

    @Override
    public void execute(AttackContext ctx, AttackStep next) {
        if (ctx.isCancelled()) { if (next != null) next.execute(ctx, null); return; }

        CardSnapshot defenderCard = ctx.getBoardState().getCardCache()
                .get(ctx.getDefender().getCardId());
        CardSnapshot attackerCard = ctx.getBoardState().getCardCache()
                .get(ctx.getAttacker().getCardId());

        if (defenderCard != null && defenderCard.getWeaknessType() != null
                && attackerCard != null && attackerCard.getTypes() != null) {

            List<String> attackerTypes = attackerCard.getTypes();
            String weakType = defenderCard.getWeaknessType();

            if (attackerTypes.contains(weakType)) {
                // XY format: ×2
                ctx.setModifiedDamage(ctx.getModifiedDamage() * 2);
            }
        }

        if (next != null) next.execute(ctx, null);
    }
}

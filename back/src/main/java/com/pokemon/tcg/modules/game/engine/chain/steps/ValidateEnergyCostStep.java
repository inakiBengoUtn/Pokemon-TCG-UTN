package com.pokemon.tcg.modules.game.engine.chain.steps;

import com.pokemon.tcg.modules.game.engine.chain.AttackContext;
import com.pokemon.tcg.modules.game.engine.chain.AttackStep;
import com.pokemon.tcg.modules.game.engine.model.CardInstance;
import com.pokemon.tcg.modules.game.engine.model.CardSnapshot;
import com.pokemon.tcg.modules.game.engine.model.PokemonInPlay;

import java.util.ArrayList;
import java.util.List;

/**
 * Step 1: Check that the attacking Pokémon has enough attached energies to pay the attack cost.
 * Colorless energy can be satisfied by any type.
 */
public class ValidateEnergyCostStep implements AttackStep {

    @Override
    public void execute(AttackContext ctx, AttackStep next) {
        if (ctx.isCancelled()) { if (next != null) next.execute(ctx, null); return; }

        PokemonInPlay attacker = ctx.getAttacker();
        List<String> required = ctx.getAttack().getCost() != null
                ? new ArrayList<>(ctx.getAttack().getCost()) : new ArrayList<>();

        // Build list of available energy types from attached energies
        List<String> available = new ArrayList<>();
        for (CardInstance energyInstance : attacker.getAttachedEnergies()) {
            CardSnapshot energyCard = ctx.getBoardState().getCardCache().get(energyInstance.getCardId());
            if (energyCard != null && energyCard.getTypes() != null) {
                available.addAll(energyCard.getTypes());
            } else {
                available.add("Colorless"); // fallback for unknown energy
            }
        }

        // Satisfy specific energy requirements first, then Colorless
        List<String> remaining = new ArrayList<>(available);
        List<String> unmet = new ArrayList<>();

        for (String req : required) {
            if ("Colorless".equals(req)) continue; // handle after
            boolean satisfied = remaining.remove(req);
            if (!satisfied) {
                unmet.add(req);
            }
        }

        // Count colorless requirement
        long colorlessNeeded = required.stream().filter("Colorless"::equals).count();
        long colorlessAvailable = remaining.size(); // anything leftover can pay colorless

        if (!unmet.isEmpty() || colorlessAvailable < colorlessNeeded) {
            ctx.setCancelled(true);
            ctx.setCancelReason("Energía insuficiente para usar " + ctx.getAttack().getName());
            return;
        }

        if (next != null) next.execute(ctx, null);
    }
}

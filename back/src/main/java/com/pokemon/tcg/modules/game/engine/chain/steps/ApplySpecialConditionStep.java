package com.pokemon.tcg.modules.game.engine.chain.steps;

import com.pokemon.tcg.modules.game.engine.chain.AttackContext;
import com.pokemon.tcg.modules.game.engine.chain.AttackStep;
import com.pokemon.tcg.modules.game.engine.model.PokemonInPlay;

import java.util.Random;

/**
 * Step 2: Resolve special conditions that affect whether the attacker can attack.
 * <ul>
 *   <li>Paralyzed → cannot attack at all.</li>
 *   <li>Asleep → cannot attack at all.</li>
 *   <li>Confused → flip a coin. Heads: attack proceeds. Tails: 30 damage to self, attack fails.</li>
 * </ul>
 */
public class ApplySpecialConditionStep implements AttackStep {

    private final Random rng;

    public ApplySpecialConditionStep() { this.rng = new Random(); }
    public ApplySpecialConditionStep(Random rng) { this.rng = rng; }

    @Override
    public void execute(AttackContext ctx, AttackStep next) {
        if (ctx.isCancelled()) { if (next != null) next.execute(ctx, null); return; }

        PokemonInPlay attacker = ctx.getAttacker();

        if (attacker.isParalyzed()) {
            ctx.setCancelled(true);
            ctx.setCancelReason(attacker.getCardId() + " está paralizado y no puede atacar.");
            return;
        }

        if (attacker.isAsleep()) {
            ctx.setCancelled(true);
            ctx.setCancelReason(attacker.getCardId() + " está dormido y no puede atacar.");
            return;
        }

        if (attacker.isConfused()) {
            boolean heads = rng.nextBoolean();
            if (!heads) {
                // Tails: 30 damage to self, attack fails
                int selfDmg = 30;
                attacker.setCurrentHp(Math.max(0, attacker.getCurrentHp() - selfDmg));
                ctx.setSelfDamage(selfDmg);
                ctx.setCancelled(true);
                ctx.setCancelReason("Confusión — el Pokémon se lastimó a sí mismo (30 daño) y no atacó.");
                return;
            }
            // Heads: attack proceeds normally
        }

        if (next != null) next.execute(ctx, null);
    }
}

package com.pokemon.tcg.modules.game.engine.service;

import com.pokemon.tcg.modules.game.engine.chain.AttackContext;
import com.pokemon.tcg.modules.game.engine.chain.AttackStep;
import com.pokemon.tcg.modules.game.engine.chain.steps.*;
import com.pokemon.tcg.modules.game.engine.model.AttackSnapshot;
import com.pokemon.tcg.modules.game.engine.model.BoardState;
import com.pokemon.tcg.modules.game.engine.model.PokemonInPlay;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * Orchestrates the 7-step attack Chain of Responsibility and returns the populated context.
 *
 * Steps:
 * 1. ValidateEnergyCostStep   — enough energy attached?
 * 2. ApplySpecialConditionStep — paralysis/sleep cancel; confusion coin flip
 * 3. CalculateBaseDamageStep  — parse damage string to int
 * 4. ApplyWeaknessStep        — ×2 if defender is weak to attacker's type
 * 5. ApplyResistanceStep      — -20 if defender resists attacker's type
 * 6. ApplyDamageStep          — subtract from defender HP
 * 7. CheckKnockoutStep        — flag KO if HP ≤ 0
 */
@Service
public class DamageCalculator {

    private final Random rng;

    public DamageCalculator() { this.rng = new Random(); }
    public DamageCalculator(Random rng) { this.rng = rng; }

    public AttackContext calculate(BoardState state, String attackingPlayerId,
                                   AttackSnapshot attack,
                                   PokemonInPlay attacker, PokemonInPlay defender) {

        AttackContext ctx = AttackContext.builder()
                .boardState(state)
                .attackingPlayerId(attackingPlayerId)
                .attack(attack)
                .attacker(attacker)
                .defender(defender)
                .build();

        // Build the chain (right to left so each step can call next)
        CheckKnockoutStep step7 = new CheckKnockoutStep();
        ApplyDamageStep step6 = new ApplyDamageStep();
        ApplyResistanceStep step5 = new ApplyResistanceStep();
        ApplyWeaknessStep step4 = new ApplyWeaknessStep();
        CalculateBaseDamageStep step3 = new CalculateBaseDamageStep();
        ApplySpecialConditionStep step2 = new ApplySpecialConditionStep(rng);
        ValidateEnergyCostStep step1 = new ValidateEnergyCostStep();

        // Wire chain as a simple lambda chain using a helper list
        List<AttackStep> chain = List.of(step1, step2, step3, step4, step5, step6, step7);
        runChain(ctx, chain, 0);

        return ctx;
    }

    private void runChain(AttackContext ctx, List<AttackStep> chain, int index) {
        if (index >= chain.size()) return;
        AttackStep current = chain.get(index);
        current.execute(ctx, (c, ignored) -> runChain(c, chain, index + 1));
    }
}

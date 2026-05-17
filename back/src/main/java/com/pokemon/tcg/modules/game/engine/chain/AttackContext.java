package com.pokemon.tcg.modules.game.engine.chain;

import com.pokemon.tcg.modules.game.engine.model.AttackSnapshot;
import com.pokemon.tcg.modules.game.engine.model.BoardState;
import com.pokemon.tcg.modules.game.engine.model.PokemonInPlay;
import lombok.*;

/**
 * Mutable context passed through every step of the attack Chain of Responsibility.
 * Each step reads from and writes to this object before calling the next step.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttackContext {

    private BoardState boardState;
    private String attackingPlayerId;
    private AttackSnapshot attack;

    // Populated by steps
    private PokemonInPlay attacker;
    private PokemonInPlay defender;

    // Damage values modified by each step
    private int baseDamage;
    private int modifiedDamage;

    // If true, the attack is cancelled (e.g. confusion heads, paralysis)
    private boolean cancelled;
    private String cancelReason;

    // If the defender is knocked out after damage is applied
    private boolean defenderKnockedOut;

    // Self-damage applied to attacker (e.g. confusion tails)
    private int selfDamage;
}

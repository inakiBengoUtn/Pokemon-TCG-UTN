package com.pokemon.tcg.modules.game.engine.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PokemonInPlay {

    private String instanceId;  // unique per physical copy on the field
    private String cardId;

    private int currentHp;
    private int maxHp;

    @Builder.Default
    private List<CardInstance> attachedEnergies = new ArrayList<>();

    private CardInstance attachedTool; // null if none

    // Special conditions
    // Asleep / Confused / Paralyzed are mutually exclusive — only one at a time
    private boolean asleep;
    private boolean confused;
    private boolean paralyzed;

    // Burned and Poisoned can coexist with each other and with the above
    private boolean burned;
    private boolean poisoned;

    // Game-turn when this Pokémon was placed on the bench or last evolved
    // Used to enforce "cannot evolve the same turn it was played"
    private int turnPlacedOrEvolved;

    // Stack of previous evolution stages still under this card
    // index 0 = original Basic, last = current stage (mirrors the card pile under the active)
    @Builder.Default
    private List<String> evolutionStack = new ArrayList<>(); // instanceIds from bottom to top

    public boolean isKnockedOut() {
        return currentHp <= 0;
    }

    public int damageCounters() {
        return (maxHp - currentHp) / 10;
    }

    /** Remove a mutually-exclusive condition before applying Asleep/Confused/Paralyzed. */
    public void clearVolatileConditions() {
        asleep = false;
        confused = false;
        paralyzed = false;
    }
}

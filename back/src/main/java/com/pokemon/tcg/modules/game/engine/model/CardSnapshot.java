package com.pokemon.tcg.modules.game.engine.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardSnapshot {
    private String cardId;
    private String name;
    private String supertype;         // "POKEMON", "ENERGY", "TRAINER"
    private List<String> subtypes;
    private List<String> types;       // energy types for Pokémon (e.g. ["Fire"])
    private Integer hp;
    private String evolvesFrom;
    private Integer retreatCost;
    private List<AttackSnapshot> attacks;
    private List<AbilitySnapshot> abilities;
    private String weaknessType;
    private String weaknessValue;     // "×2"
    private String resistanceType;
    private String resistanceValue;   // "-20"
    private String imageUrlSmall;
    private boolean basicEnergy;
    private boolean aceTactico;

    public boolean isBasicPokemon() {
        return "POKEMON".equals(supertype)
                && subtypes != null
                && subtypes.contains("Basic");
    }

    public boolean isStage1() {
        return "POKEMON".equals(supertype)
                && subtypes != null
                && subtypes.contains("Stage 1");
    }

    public boolean isStage2() {
        return "POKEMON".equals(supertype)
                && subtypes != null
                && subtypes.contains("Stage 2");
    }
}

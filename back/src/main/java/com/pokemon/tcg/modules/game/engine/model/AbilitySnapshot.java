package com.pokemon.tcg.modules.game.engine.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AbilitySnapshot {
    private String name;
    private String text;
    private String type; // "Ability", "Poke-Power", "Poke-Body"
}

package com.pokemon.tcg.modules.game.engine.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttackSnapshot {
    private String name;
    private List<String> cost;       // energy types required, e.g. ["Fire","Colorless"]
    private int convertedEnergyCost;
    private String damage;           // "60", "20+", "60×", "" (empty = no direct damage)
    private String text;
}

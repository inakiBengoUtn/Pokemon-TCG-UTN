package com.pokemon.tcg.modules.game.engine.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardInstance {
    private String instanceId; // UUID as string — unique per physical card copy
    private String cardId;     // reference to Card entity (e.g. "xy1-1")
}

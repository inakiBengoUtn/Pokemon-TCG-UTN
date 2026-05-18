package com.pokemon.tcg.modules.game.domain.models.Card;

import com.pokemon.tcg.modules.game.domain.models.Element;
import lombok.Builder;

import java.util.List;

@Builder
public class Attack {
    private String name;
    private List<Element> cost;
    private Integer damage;
}

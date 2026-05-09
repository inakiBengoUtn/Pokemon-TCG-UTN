package com.pokemon.tcg.modules.game.domain.models.Card;

import com.pokemon.tcg.modules.game.domain.models.Element;

import java.util.List;

public class Attack {
    private String name;
    private List<Element> cost;
    private Integer damage;
    private Integer effectId;
}

package com.pokemon.tcg.modules.catalog.models;

import com.pokemon.tcg.modules.game.domain.Element;
import lombok.Builder;

import java.util.List;

@Builder
public class Attack {
    private String name;
    private List<Element> cost;
    private Integer damage;
}

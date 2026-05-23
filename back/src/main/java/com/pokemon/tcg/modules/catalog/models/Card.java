package com.pokemon.tcg.modules.catalog.models;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public abstract class Card {
    private String id;
    private String name;
    private Supertype supertype;
    private String image;
}

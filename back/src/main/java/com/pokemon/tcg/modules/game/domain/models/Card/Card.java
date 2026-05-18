package com.pokemon.tcg.modules.game.domain.models.Card;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class Card {
    private String id;
    private String name;
    private Supertype supertype;
    private String image;
}

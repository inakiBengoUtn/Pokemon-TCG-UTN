package com.pokemon.tcg.modules.game.domain.models.Card;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
abstract class Card {
    private String id;
    private String name;
    private String supertype;
    private String image;
}

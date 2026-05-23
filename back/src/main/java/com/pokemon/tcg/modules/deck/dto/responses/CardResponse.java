package com.pokemon.tcg.modules.deck.dto.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardResponse {
    private String card_id;
    private String image;
    private String name;
    private Integer quantity;
}

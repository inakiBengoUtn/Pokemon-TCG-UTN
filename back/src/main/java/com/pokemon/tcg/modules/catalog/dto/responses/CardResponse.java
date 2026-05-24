package com.pokemon.tcg.modules.catalog.dto.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardResponse {
    private String id;
    private String image;
    private String name;
    private Integer quantity;
}

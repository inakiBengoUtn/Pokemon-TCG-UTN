package com.pokemon.tcg.modules.catalog.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class DeckResponse {
    private UUID id;
    private String name;
    @JsonProperty("cover_image")
    private String coverImage;
    private Integer quantity;
    private List<CardResponse> cards;
}

package com.pokemon.tcg.modules.deck.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeckResponse {
    private UUID id;
    private String name;
    @JsonProperty("cover_image")
    private String coverImage;
//    private List<CardResponse> cards;
}

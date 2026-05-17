package com.pokemon.tcg.modules.deck.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DeckValidationResponse {
    private boolean valid;
    private int totalCards;
    private List<String> errors;
}

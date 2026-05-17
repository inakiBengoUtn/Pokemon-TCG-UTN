package com.pokemon.tcg.modules.deck.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeckCardRequest {

    @NotBlank(message = "Card ID is required")
    private String cardId;

    @Positive
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}

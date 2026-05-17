package com.pokemon.tcg.modules.deck.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SaveDeckRequest {

    @NotBlank(message = "Deck name is required")
    @Size(max = 50, message = "Deck name must not exceed 50 characters")
    private String name;

    @NotEmpty(message = "Deck must have at least one card")
    @Valid
    private List<DeckCardRequest> cards;
}

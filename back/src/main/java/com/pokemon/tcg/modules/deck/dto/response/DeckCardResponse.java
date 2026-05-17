package com.pokemon.tcg.modules.deck.dto.response;

import com.pokemon.tcg.modules.card.dto.CardResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeckCardResponse {
    private CardResponse card;
    private int quantity;
}

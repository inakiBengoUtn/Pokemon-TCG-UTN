package com.pokemon.tcg.modules.deck.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class DeckResponse {
    private UUID id;
    private String name;
    private String ownerUsername;
    private int totalCards;
    private List<DeckCardResponse> cards;
    private DeckValidationResponse validation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

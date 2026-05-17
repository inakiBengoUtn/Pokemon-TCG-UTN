package com.pokemon.tcg.modules.deck.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class DeckSummaryResponse {
    private UUID id;
    private String name;
    private int totalCards;
    private boolean valid;
    private LocalDateTime updatedAt;
}

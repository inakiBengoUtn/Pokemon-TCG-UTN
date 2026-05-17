package com.pokemon.tcg.modules.deck.controller;

import com.pokemon.tcg.modules.deck.dto.request.SaveDeckRequest;
import com.pokemon.tcg.modules.deck.dto.response.DeckResponse;
import com.pokemon.tcg.modules.deck.dto.response.DeckSummaryResponse;
import com.pokemon.tcg.modules.deck.dto.response.DeckValidationResponse;
import com.pokemon.tcg.modules.deck.service.DeckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/decks")
@RequiredArgsConstructor
public class DeckController {

    private final DeckService deckService;

    @GetMapping
    public ResponseEntity<List<DeckSummaryResponse>> getDecks(Principal principal) {
        return ResponseEntity.ok(deckService.getDecks(principal.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeckResponse> getDeck(Principal principal, @PathVariable UUID id) {
        return ResponseEntity.ok(deckService.getDeck(principal.getName(), id));
    }

    @PostMapping
    public ResponseEntity<DeckResponse> createDeck(
            Principal principal,
            @Valid @RequestBody SaveDeckRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deckService.createDeck(principal.getName(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeckResponse> updateDeck(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody SaveDeckRequest request) {
        return ResponseEntity.ok(deckService.updateDeck(principal.getName(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeck(Principal principal, @PathVariable UUID id) {
        deckService.deleteDeck(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/validate")
    public ResponseEntity<DeckValidationResponse> validateDeck(
            Principal principal, @PathVariable UUID id) {
        return ResponseEntity.ok(deckService.validateDeck(principal.getName(), id));
    }
}

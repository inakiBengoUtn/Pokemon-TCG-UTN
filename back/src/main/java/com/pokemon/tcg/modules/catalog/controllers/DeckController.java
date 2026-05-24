package com.pokemon.tcg.modules.catalog.controllers;

import com.pokemon.tcg.modules.catalog.dto.responses.DeckResponse;
import com.pokemon.tcg.modules.catalog.dto.responses.DeckSummaryResponse;
import com.pokemon.tcg.modules.catalog.services.DeckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/decks")
@RequiredArgsConstructor
public class DeckController {
    private final DeckService deckService;

    @GetMapping
    public ResponseEntity<List<DeckSummaryResponse>> getAllDecks(Principal principal) {
        List<DeckSummaryResponse> response = deckService.getAllDecks(principal.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeckResponse> getDeckById(@PathVariable String id) {
        DeckResponse deckResponse = deckService.getDeck(id);
        return ResponseEntity.ok(deckResponse);
    }
}

package com.pokemon.tcg.modules.deck.controllers;

import com.pokemon.tcg.modules.deck.dto.responses.DeckResponse;
import com.pokemon.tcg.modules.deck.services.DeckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<List<DeckResponse>> getAllDecks(Principal principal) {
        List<DeckResponse> response = deckService.getAllDecks(principal.getName());
        return ResponseEntity.ok(response);
    }
}

package com.pokemon.tcg.modules.card.controller;

import com.pokemon.tcg.modules.card.dto.CardResponse;
import com.pokemon.tcg.modules.card.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping
    public ResponseEntity<List<CardResponse>> searchCards(
            @RequestParam(defaultValue = "xy1") String setId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String supertype) {
        return ResponseEntity.ok(cardService.searchCards(setId, name, supertype));
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncCards(
            @RequestParam(defaultValue = "xy1") String setId,
            @RequestParam(defaultValue = "false") boolean force) {
        int count = cardService.syncSet(setId, force);
        return ResponseEntity.ok(Map.of(
                "setId", setId,
                "synced", count,
                "message", count > 0 ? "Sync completed" : "Already cached"
        ));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> cacheStatus(
            @RequestParam(defaultValue = "xy1") String setId) {
        return ResponseEntity.ok(Map.of(
                "setId", setId,
                "cached", cardService.isSetCached(setId)
        ));
    }
}

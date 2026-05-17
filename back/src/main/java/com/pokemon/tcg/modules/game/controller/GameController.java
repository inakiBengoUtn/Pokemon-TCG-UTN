package com.pokemon.tcg.modules.game.controller;

import com.pokemon.tcg.modules.game.dto.request.GameActionRequest;
import com.pokemon.tcg.modules.game.engine.exception.GameNotFoundException;
import com.pokemon.tcg.modules.game.engine.exception.InvalidActionException;
import com.pokemon.tcg.modules.game.engine.model.BoardState;
import com.pokemon.tcg.modules.game.engine.service.GameEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

/**
 * REST API for game actions.
 * All endpoints require authentication (JWT cookie).
 */
@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameEngine gameEngine;

    /** Initialize a match (called by server after both players are matched). */
    @PostMapping("/{matchId}/init")
    public ResponseEntity<BoardState> initializeGame(@PathVariable UUID matchId) {
        BoardState state = gameEngine.initializeGame(matchId);
        return ResponseEntity.ok(state);
    }

    /** Perform an in-game action. */
    @PostMapping("/{matchId}/action")
    public ResponseEntity<BoardState> performAction(
            @PathVariable UUID matchId,
            @RequestBody GameActionRequest request,
            Principal principal) {

        String playerId = principal.getName();
        BoardState state = gameEngine.performAction(matchId, playerId, request);
        return ResponseEntity.ok(state);
    }

    /** Get current game state (full board — cards in opponent's hand are hidden). */
    @GetMapping("/{matchId}/state")
    public ResponseEntity<BoardState> getState(
            @PathVariable UUID matchId,
            Principal principal) {

        BoardState state = gameEngine.getGameState(matchId);
        // Hide opponent's hand (replace with count only)
        hideOpponentHand(state, principal.getName());
        return ResponseEntity.ok(state);
    }

    // ---- exception handlers ----

    @ExceptionHandler(InvalidActionException.class)
    public ResponseEntity<Map<String, String>> handleInvalidAction(InvalidActionException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(GameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    private void hideOpponentHand(BoardState state, String playerId) {
        // Replace the opponent's hand with an empty list to avoid leaking card info
        // (the frontend only needs the count, which it can derive from hand.size())
        if (!playerId.equals(state.getPlayer1Id())) {
            state.getPlayer1Board().setHand(java.util.List.of());
        }
        if (!playerId.equals(state.getPlayer2Id())) {
            state.getPlayer2Board().setHand(java.util.List.of());
        }
    }
}

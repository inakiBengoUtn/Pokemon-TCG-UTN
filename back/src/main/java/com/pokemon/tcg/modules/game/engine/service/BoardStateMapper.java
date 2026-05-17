package com.pokemon.tcg.modules.game.engine.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemon.tcg.modules.card.model.Card;
import com.pokemon.tcg.modules.deck.model.DeckCard;
import com.pokemon.tcg.modules.game.engine.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Converts JPA entities (Card, DeckCard) into board-state POJOs (CardSnapshot, CardInstance)
 * and builds the initial {@link PlayerBoard} for each player at game start.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BoardStateMapper {

    private final ObjectMapper objectMapper;

    /** Build a CardSnapshot from a Card JPA entity (used to populate the card cache). */
    public CardSnapshot toSnapshot(Card card) {
        return CardSnapshot.builder()
                .cardId(card.getId())
                .name(card.getName())
                .supertype(card.getSupertype().name())
                .subtypes(card.getSubtypes())
                .types(card.getTypes())
                .hp(card.getHp())
                .evolvesFrom(card.getEvolvesFrom())
                .retreatCost(card.getRetreatCost())
                .attacks(parseAttacks(card.getAttacksJson()))
                .abilities(parseAbilities(card.getAbilitiesJson()))
                .weaknessType(card.getWeaknessType())
                .weaknessValue(card.getWeaknessValue())
                .resistanceType(card.getResistanceType())
                .resistanceValue(card.getResistanceValue())
                .imageUrlSmall(card.getImageUrlSmall())
                .basicEnergy(card.isBasicEnergy())
                .aceTactico(card.isAceTactico())
                .build();
    }

    /**
     * Build the initial {@link PlayerBoard} for a player:
     * <ul>
     *   <li>Assign a unique instanceId to every card in the deck (60 cards).</li>
     *   <li>Shuffle the deck.</li>
     *   <li>Draw 7 cards into hand.</li>
     *   <li>Set aside 6 prize cards from the top of the shuffled deck.</li>
     * </ul>
     */
    public PlayerBoard buildInitialBoard(String playerId, List<DeckCard> deckCards,
                                          Map<String, CardSnapshot> cacheOut) {
        // Expand deck into individual CardInstance objects (one per physical card copy)
        List<CardInstance> allCards = new ArrayList<>();
        for (DeckCard dc : deckCards) {
            CardSnapshot snapshot = toSnapshot(dc.getCard());
            cacheOut.put(dc.getCard().getId(), snapshot);

            for (int i = 0; i < dc.getQuantity(); i++) {
                allCards.add(CardInstance.builder()
                        .instanceId(UUID.randomUUID().toString())
                        .cardId(dc.getCard().getId())
                        .build());
            }
        }

        // Shuffle
        Collections.shuffle(allCards);

        // Draw 7 into hand
        List<CardInstance> hand = new ArrayList<>(allCards.subList(0, Math.min(7, allCards.size())));
        List<CardInstance> remaining = new ArrayList<>(allCards.subList(Math.min(7, allCards.size()), allCards.size()));

        // Set 6 prize cards aside
        List<CardInstance> prizes = new ArrayList<>(remaining.subList(0, Math.min(6, remaining.size())));
        List<CardInstance> deck = new ArrayList<>(remaining.subList(Math.min(6, remaining.size()), remaining.size()));

        return PlayerBoard.builder()
                .playerId(playerId)
                .hand(hand)
                .deck(deck)
                .prizes(prizes)
                .discard(new ArrayList<>())
                .bench(new ArrayList<>())
                .activePokemon(null)
                .build();
    }

    // ---- JSON parsing helpers ----

    private List<AttackSnapshot> parseAttacks(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            log.warn("Failed to parse attacksJson: {}", e.getMessage());
            return List.of();
        }
    }

    private List<AbilitySnapshot> parseAbilities(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            log.warn("Failed to parse abilitiesJson: {}", e.getMessage());
            return List.of();
        }
    }
}

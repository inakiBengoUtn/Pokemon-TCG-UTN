package com.pokemon.tcg.modules.game.engine.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerBoard {

    private String playerId;

    // Field
    private PokemonInPlay activePokemon;       // null during setup phase

    @Builder.Default
    private List<PokemonInPlay> bench = new ArrayList<>();  // max 5

    // Card zones — ordered; deck[0] = top of deck
    @Builder.Default
    private List<CardInstance> hand = new ArrayList<>();

    @Builder.Default
    private List<CardInstance> deck = new ArrayList<>();

    @Builder.Default
    private List<CardInstance> discard = new ArrayList<>();

    @Builder.Default
    private List<CardInstance> prizes = new ArrayList<>();  // 6 at game start

    // Per-turn action flags (reset at the start of each of this player's turns)
    private boolean hasPlayedEnergy;
    private boolean hasPlayedSupporter;
    private boolean hasRetreated;
    private boolean hasAttacked;

    // Setup phase: true once the player has placed their active pokemon
    private boolean setupComplete;

    // Mulligan counter (informational)
    private int mulliganCount;

    // ---- helpers ----

    public int prizesRemaining() {
        return prizes.size();
    }

    public boolean hasBenchSpace() {
        return bench.size() < 5;
    }

    public boolean hasBasicInHand(Map<String, CardSnapshot> cache) {
        return hand.stream()
                .map(ci -> cache.get(ci.getCardId()))
                .filter(cs -> cs != null)
                .anyMatch(CardSnapshot::isBasicPokemon);
    }

    public void resetTurnFlags() {
        hasPlayedEnergy = false;
        hasPlayedSupporter = false;
        hasRetreated = false;
        hasAttacked = false;
    }

    /** Draw the top card of the deck into hand. Returns the drawn instance, or null if deck empty. */
    public CardInstance drawCard() {
        if (deck.isEmpty()) return null;
        CardInstance drawn = deck.remove(0);
        hand.add(drawn);
        return drawn;
    }

    /** Move a card from hand to discard by instanceId. */
    public boolean discardFromHand(String instanceId) {
        CardInstance ci = hand.stream()
                .filter(c -> c.getInstanceId().equals(instanceId))
                .findFirst().orElse(null);
        if (ci == null) return false;
        hand.remove(ci);
        discard.add(ci);
        return true;
    }

    /** Find a card in hand by instanceId. */
    public CardInstance findInHand(String instanceId) {
        return hand.stream()
                .filter(c -> c.getInstanceId().equals(instanceId))
                .findFirst().orElse(null);
    }

    /** Find a pokemon in play (active or bench) by instanceId. */
    public PokemonInPlay findPokemonInPlay(String instanceId) {
        if (activePokemon != null && activePokemon.getInstanceId().equals(instanceId)) {
            return activePokemon;
        }
        return bench.stream()
                .filter(p -> p.getInstanceId().equals(instanceId))
                .findFirst().orElse(null);
    }

    /** All pokemon currently in play (active + bench). */
    public List<PokemonInPlay> allPokemonInPlay() {
        List<PokemonInPlay> all = new ArrayList<>();
        if (activePokemon != null) all.add(activePokemon);
        all.addAll(bench);
        return all;
    }

    /** True if this player has no pokemon at all in play. */
    public boolean hasNoPokemon() {
        return activePokemon == null && bench.isEmpty();
    }

    /** Take the top prize card and move it to hand. Returns the prize taken, or null if none left. */
    public CardInstance takePrize() {
        if (prizes.isEmpty()) return null;
        CardInstance prize = prizes.remove(0);
        hand.add(prize);
        return prize;
    }
}

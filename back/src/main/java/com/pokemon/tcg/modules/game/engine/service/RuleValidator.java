package com.pokemon.tcg.modules.game.engine.service;

import com.pokemon.tcg.common.enums.TurnPhase;
import com.pokemon.tcg.modules.game.engine.exception.InvalidActionException;
import com.pokemon.tcg.modules.game.engine.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Validates game actions against the XY Unlimited ruleset before they are executed.
 * Throws {@link InvalidActionException} when a rule is violated.
 */
@Service
public class RuleValidator {

    public void validatePlaceBasic(BoardState state, String playerId, CardSnapshot card) {
        PlayerBoard board = state.getBoardFor(playerId);

        if (!card.isBasicPokemon()) {
            throw new InvalidActionException("Solo se puede colocar un Pokémon Básico directamente.");
        }

        if (board.getActivePokemon() == null) {
            // Placing as the active — allowed during setup or if active was KO'd
            return;
        }

        if (!board.hasBenchSpace()) {
            throw new InvalidActionException("El banco está lleno (máximo 5 Pokémon).");
        }
    }

    public void validateAttachEnergy(BoardState state, String playerId) {
        PlayerBoard board = state.getBoardFor(playerId);
        if (board.isHasPlayedEnergy()) {
            throw new InvalidActionException("Solo se puede adjuntar 1 carta de Energía por turno.");
        }
        requirePhase(state, TurnPhase.MAIN);
    }

    public void validateEvolve(BoardState state, String playerId,
                               CardSnapshot evolutionCard, PokemonInPlay target) {
        requirePhase(state, TurnPhase.MAIN);

        CardSnapshot targetCard = state.getCardCache().get(target.getCardId());
        if (targetCard == null) {
            throw new InvalidActionException("Carta del Pokémon objetivo no encontrada.");
        }

        // Evolution chain check: evolvesFrom must match the target's name
        String evolvesFrom = evolutionCard.getEvolvesFrom();
        if (evolvesFrom == null || !evolvesFrom.equalsIgnoreCase(targetCard.getName())) {
            throw new InvalidActionException(
                    evolutionCard.getName() + " no evoluciona de " + targetCard.getName() + ".");
        }

        // Cannot evolve the same turn the Pokémon was played
        if (target.getTurnPlacedOrEvolved() >= state.getTurnNumber()) {
            throw new InvalidActionException(
                    "No se puede evolucionar un Pokémon el mismo turno en que fue colocado.");
        }

        // Cannot evolve on the very first turn of the game
        if (state.getTurnNumber() == 1) {
            throw new InvalidActionException("No se puede evolucionar durante el primer turno.");
        }
    }

    public void validatePlaySupporter(BoardState state, String playerId) {
        PlayerBoard board = state.getBoardFor(playerId);
        requirePhase(state, TurnPhase.MAIN);
        if (board.isHasPlayedSupporter()) {
            throw new InvalidActionException("Solo se puede jugar 1 carta de Entrenador Seguidor por turno.");
        }
    }

    public void validatePlayItem(BoardState state, String playerId, CardSnapshot card) {
        requirePhase(state, TurnPhase.MAIN);
        List<String> subtypes = card.getSubtypes();
        if (subtypes == null || !subtypes.contains("Item")) {
            throw new InvalidActionException("Esa carta no es un Objeto (Item).");
        }
    }

    public void validateRetreat(BoardState state, String playerId, PokemonInPlay newActive) {
        requirePhase(state, TurnPhase.MAIN);
        PlayerBoard board = state.getBoardFor(playerId);

        if (board.isHasRetreated()) {
            throw new InvalidActionException("Solo se puede retirar 1 vez por turno.");
        }

        PokemonInPlay active = board.getActivePokemon();
        if (active == null) {
            throw new InvalidActionException("No hay un Pokémon activo para retirar.");
        }

        if (active.isParalyzed()) {
            throw new InvalidActionException("Un Pokémon paralizado no puede retirarse.");
        }

        if (active.isAsleep()) {
            throw new InvalidActionException("Un Pokémon dormido no puede retirarse.");
        }

        // Check retreat cost can be paid
        int cost = getCostFromCache(state, active.getCardId());
        int availableEnergy = active.getAttachedEnergies().size();
        if (availableEnergy < cost) {
            throw new InvalidActionException(
                    "Se necesitan " + cost + " energías para retirarse; el Pokémon tiene " + availableEnergy + ".");
        }

        if (newActive == null) {
            throw new InvalidActionException("Debes elegir un Pokémon del banco para pasar al activo.");
        }

        boolean onBench = board.getBench().stream()
                .anyMatch(p -> p.getInstanceId().equals(newActive.getInstanceId()));
        if (!onBench) {
            throw new InvalidActionException("El Pokémon elegido no está en el banco.");
        }
    }

    public void validateAttack(BoardState state, String playerId, int attackIndex) {
        requirePhase(state, TurnPhase.ATTACK);
        PlayerBoard board = state.getBoardFor(playerId);
        PokemonInPlay active = board.getActivePokemon();

        if (active == null) {
            throw new InvalidActionException("No hay un Pokémon activo para atacar.");
        }

        if (board.isHasAttacked()) {
            throw new InvalidActionException("Ya se atacó este turno.");
        }

        CardSnapshot card = state.getCardCache().get(active.getCardId());
        if (card == null || card.getAttacks() == null || card.getAttacks().isEmpty()) {
            throw new InvalidActionException("El Pokémon activo no tiene ataques disponibles.");
        }

        if (attackIndex < 0 || attackIndex >= card.getAttacks().size()) {
            throw new InvalidActionException("Índice de ataque inválido: " + attackIndex);
        }
    }

    public void validateEndTurn(BoardState state, String playerId) {
        if (!playerId.equals(state.getCurrentTurnPlayerId())) {
            throw new InvalidActionException("No es tu turno.");
        }
    }

    // ---- private helpers ----

    private void requirePhase(BoardState state, TurnPhase required) {
        if (!required.name().equals(state.getTurnPhase())) {
            throw new InvalidActionException(
                    "Esta acción solo se puede realizar en la fase " + required.name() +
                    " (fase actual: " + state.getTurnPhase() + ").");
        }
    }

    private int getCostFromCache(BoardState state, String cardId) {
        CardSnapshot cs = state.getCardCache().get(cardId);
        return cs != null && cs.getRetreatCost() != null ? cs.getRetreatCost() : 0;
    }
}

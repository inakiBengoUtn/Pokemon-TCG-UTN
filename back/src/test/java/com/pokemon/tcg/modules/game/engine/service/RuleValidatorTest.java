package com.pokemon.tcg.modules.game.engine.service;

import com.pokemon.tcg.common.enums.TurnPhase;
import com.pokemon.tcg.modules.game.engine.exception.InvalidActionException;
import com.pokemon.tcg.modules.game.engine.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class RuleValidatorTest {

    private RuleValidator validator;
    private BoardState state;
    private PlayerBoard playerBoard;

    @BeforeEach
    void setUp() {
        validator = new RuleValidator();

        playerBoard = PlayerBoard.builder()
                .playerId("player1")
                .bench(new ArrayList<>())
                .hand(new ArrayList<>())
                .deck(new ArrayList<>())
                .discard(new ArrayList<>())
                .prizes(new ArrayList<>())
                .build();

        state = BoardState.builder()
                .matchId("match1")
                .player1Id("player1")
                .player2Id("player2")
                .player1Board(playerBoard)
                .player2Board(PlayerBoard.builder()
                        .playerId("player2")
                        .bench(new ArrayList<>()).hand(new ArrayList<>())
                        .deck(new ArrayList<>()).discard(new ArrayList<>())
                        .prizes(new ArrayList<>()).build())
                .currentTurnPlayerId("player1")
                .turnNumber(2)
                .turnPhase(TurnPhase.MAIN.name())
                .cardCache(new HashMap<>())
                .build();
    }

    // ---- validatePlaceBasic ----

    @Test
    void placeBasic_validOnBench_doesNotThrow() {
        CardSnapshot basic = basicPokemon("xy1-1");
        playerBoard.setActivePokemon(existingActive());
        assertThatNoException().isThrownBy(() ->
                validator.validatePlaceBasic(state, "player1", basic));
    }

    @Test
    void placeBasic_nonBasic_throws() {
        CardSnapshot stage1 = CardSnapshot.builder()
                .cardId("xy1-2").name("Raichu").supertype("POKEMON")
                .subtypes(List.of("Stage 1")).hp(90).build();

        assertThatThrownBy(() -> validator.validatePlaceBasic(state, "player1", stage1))
                .isInstanceOf(InvalidActionException.class)
                .hasMessageContaining("Básico");
    }

    @Test
    void placeBasic_fullBench_throws() {
        playerBoard.setActivePokemon(existingActive());
        for (int i = 0; i < 5; i++) {
            playerBoard.getBench().add(existingActive());
        }
        CardSnapshot basic = basicPokemon("xy1-3");

        assertThatThrownBy(() -> validator.validatePlaceBasic(state, "player1", basic))
                .isInstanceOf(InvalidActionException.class)
                .hasMessageContaining("banco");
    }

    // ---- validateAttachEnergy ----

    @Test
    void attachEnergy_firstTime_doesNotThrow() {
        playerBoard.setHasPlayedEnergy(false);
        assertThatNoException().isThrownBy(() ->
                validator.validateAttachEnergy(state, "player1"));
    }

    @Test
    void attachEnergy_alreadyPlayed_throws() {
        playerBoard.setHasPlayedEnergy(true);
        assertThatThrownBy(() -> validator.validateAttachEnergy(state, "player1"))
                .isInstanceOf(InvalidActionException.class)
                .hasMessageContaining("1 carta de Energía");
    }

    @Test
    void attachEnergy_wrongPhase_throws() {
        state.setTurnPhase(TurnPhase.ATTACK.name());
        assertThatThrownBy(() -> validator.validateAttachEnergy(state, "player1"))
                .isInstanceOf(InvalidActionException.class)
                .hasMessageContaining("MAIN");
    }

    // ---- validateEvolve ----

    @Test
    void evolve_valid_doesNotThrow() {
        CardSnapshot raichu = CardSnapshot.builder()
                .cardId("xy1-2").name("Raichu").supertype("POKEMON")
                .subtypes(List.of("Stage 1")).evolvesFrom("Pikachu").hp(90).build();

        PokemonInPlay pikachu = PokemonInPlay.builder()
                .instanceId("pip1").cardId("xy1-1")
                .currentHp(60).maxHp(60).turnPlacedOrEvolved(1)
                .evolutionStack(new ArrayList<>())
                .attachedEnergies(new ArrayList<>())
                .build();

        state.getCardCache().put("xy1-1", basicPokemon("xy1-1"));
        // Make pikachu's card snapshot have name "Pikachu"
        state.getCardCache().put("xy1-1",
                CardSnapshot.builder().cardId("xy1-1").name("Pikachu")
                        .supertype("POKEMON").subtypes(List.of("Basic")).hp(60).build());

        assertThatNoException().isThrownBy(() ->
                validator.validateEvolve(state, "player1", raichu, pikachu));
    }

    @Test
    void evolve_wrongEvolvesFrom_throws() {
        CardSnapshot raichu = CardSnapshot.builder()
                .cardId("xy1-2").name("Raichu").supertype("POKEMON")
                .subtypes(List.of("Stage 1")).evolvesFrom("Pikachu").hp(90).build();

        PokemonInPlay charmander = PokemonInPlay.builder()
                .instanceId("pip2").cardId("xy1-10")
                .currentHp(50).maxHp(50).turnPlacedOrEvolved(1)
                .evolutionStack(new ArrayList<>()).attachedEnergies(new ArrayList<>()).build();

        state.getCardCache().put("xy1-10",
                CardSnapshot.builder().cardId("xy1-10").name("Charmander")
                        .supertype("POKEMON").subtypes(List.of("Basic")).hp(50).build());

        assertThatThrownBy(() -> validator.validateEvolve(state, "player1", raichu, charmander))
                .isInstanceOf(InvalidActionException.class)
                .hasMessageContaining("no evoluciona");
    }

    @Test
    void evolve_sameTurn_throws() {
        state.setTurnNumber(3);
        CardSnapshot raichu = CardSnapshot.builder()
                .cardId("xy1-2").name("Raichu").supertype("POKEMON")
                .subtypes(List.of("Stage 1")).evolvesFrom("Pikachu").hp(90).build();

        PokemonInPlay pikachu = PokemonInPlay.builder()
                .instanceId("pip1").cardId("xy1-1")
                .currentHp(60).maxHp(60).turnPlacedOrEvolved(3) // same turn
                .evolutionStack(new ArrayList<>()).attachedEnergies(new ArrayList<>()).build();

        state.getCardCache().put("xy1-1",
                CardSnapshot.builder().cardId("xy1-1").name("Pikachu")
                        .supertype("POKEMON").subtypes(List.of("Basic")).hp(60).build());

        assertThatThrownBy(() -> validator.validateEvolve(state, "player1", raichu, pikachu))
                .isInstanceOf(InvalidActionException.class)
                .hasMessageContaining("mismo turno");
    }

    @Test
    void evolve_firstTurn_throws() {
        state.setTurnNumber(1);
        CardSnapshot raichu = CardSnapshot.builder()
                .cardId("xy1-2").name("Raichu").supertype("POKEMON")
                .subtypes(List.of("Stage 1")).evolvesFrom("Pikachu").hp(90).build();

        PokemonInPlay pikachu = PokemonInPlay.builder()
                .instanceId("pip1").cardId("xy1-1")
                .currentHp(60).maxHp(60).turnPlacedOrEvolved(0)
                .evolutionStack(new ArrayList<>()).attachedEnergies(new ArrayList<>()).build();

        state.getCardCache().put("xy1-1",
                CardSnapshot.builder().cardId("xy1-1").name("Pikachu")
                        .supertype("POKEMON").subtypes(List.of("Basic")).hp(60).build());

        assertThatThrownBy(() -> validator.validateEvolve(state, "player1", raichu, pikachu))
                .isInstanceOf(InvalidActionException.class)
                .hasMessageContaining("primer turno");
    }

    // ---- validateRetreat ----

    @Test
    void retreat_paralyzed_throws() {
        PokemonInPlay paralyzed = PokemonInPlay.builder()
                .instanceId("a1").cardId("xy1-1")
                .currentHp(60).maxHp(60).paralyzed(true)
                .attachedEnergies(new ArrayList<>()).evolutionStack(new ArrayList<>()).build();
        playerBoard.setActivePokemon(paralyzed);
        state.getCardCache().put("xy1-1", basicPokemon("xy1-1"));

        PokemonInPlay bench = PokemonInPlay.builder().instanceId("b1").cardId("xy1-2")
                .currentHp(90).maxHp(90).attachedEnergies(new ArrayList<>())
                .evolutionStack(new ArrayList<>()).build();
        playerBoard.getBench().add(bench);

        assertThatThrownBy(() -> validator.validateRetreat(state, "player1", bench))
                .isInstanceOf(InvalidActionException.class)
                .hasMessageContaining("paralizado");
    }

    @Test
    void retreat_notEnoughEnergy_throws() {
        CardSnapshot pikachuCard = CardSnapshot.builder()
                .cardId("xy1-1").name("Pikachu").supertype("POKEMON")
                .subtypes(List.of("Basic")).hp(60).retreatCost(2).build();
        state.getCardCache().put("xy1-1", pikachuCard);

        PokemonInPlay active = PokemonInPlay.builder()
                .instanceId("a1").cardId("xy1-1").currentHp(60).maxHp(60)
                .attachedEnergies(new ArrayList<>()) // no energy attached
                .evolutionStack(new ArrayList<>()).build();
        playerBoard.setActivePokemon(active);

        PokemonInPlay bench = PokemonInPlay.builder().instanceId("b1").cardId("xy1-2")
                .currentHp(90).maxHp(90).attachedEnergies(new ArrayList<>())
                .evolutionStack(new ArrayList<>()).build();
        playerBoard.getBench().add(bench);

        assertThatThrownBy(() -> validator.validateRetreat(state, "player1", bench))
                .isInstanceOf(InvalidActionException.class)
                .hasMessageContaining("energías");
    }

    // ---- helpers ----

    private CardSnapshot basicPokemon(String id) {
        return CardSnapshot.builder()
                .cardId(id).name("Pikachu").supertype("POKEMON")
                .subtypes(List.of("Basic")).hp(60).retreatCost(1).build();
    }

    private PokemonInPlay existingActive() {
        return PokemonInPlay.builder()
                .instanceId(java.util.UUID.randomUUID().toString())
                .cardId("xy1-99").currentHp(50).maxHp(50)
                .attachedEnergies(new ArrayList<>())
                .evolutionStack(new ArrayList<>()).build();
    }
}

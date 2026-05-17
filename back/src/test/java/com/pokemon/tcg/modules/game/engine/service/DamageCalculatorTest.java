package com.pokemon.tcg.modules.game.engine.service;

import com.pokemon.tcg.modules.game.engine.chain.AttackContext;
import com.pokemon.tcg.modules.game.engine.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

class DamageCalculatorTest {

    private DamageCalculator calculator;
    private BoardState state;
    private PokemonInPlay attacker;
    private PokemonInPlay defender;

    @BeforeEach
    void setUp() {
        // Fixed RNG: always returns true (heads) so confusion doesn't interfere by default
        calculator = new DamageCalculator(new Random() {
            @Override public boolean nextBoolean() { return true; }
        });

        CardSnapshot pikachuCard = CardSnapshot.builder()
                .cardId("xy1-1").name("Pikachu").supertype("POKEMON")
                .subtypes(List.of("Basic")).types(List.of("Lightning"))
                .hp(60).retreatCost(1).build();

        CardSnapshot geodudeCard = CardSnapshot.builder()
                .cardId("xy1-50").name("Geodude").supertype("POKEMON")
                .subtypes(List.of("Basic")).types(List.of("Fighting"))
                .hp(60).weaknessType("Grass").weaknessValue("×2")
                .resistanceType(null).build();

        HashMap<String, CardSnapshot> cache = new HashMap<>();
        cache.put("xy1-1", pikachuCard);
        cache.put("xy1-50", geodudeCard);

        attacker = PokemonInPlay.builder()
                .instanceId("a1").cardId("xy1-1").currentHp(60).maxHp(60)
                .attachedEnergies(new ArrayList<>()).evolutionStack(new ArrayList<>()).build();

        defender = PokemonInPlay.builder()
                .instanceId("d1").cardId("xy1-50").currentHp(60).maxHp(60)
                .attachedEnergies(new ArrayList<>()).evolutionStack(new ArrayList<>()).build();

        PlayerBoard p1 = PlayerBoard.builder().playerId("p1")
                .activePokemon(attacker).bench(new ArrayList<>())
                .hand(new ArrayList<>()).deck(new ArrayList<>())
                .discard(new ArrayList<>()).prizes(new ArrayList<>()).build();
        PlayerBoard p2 = PlayerBoard.builder().playerId("p2")
                .activePokemon(defender).bench(new ArrayList<>())
                .hand(new ArrayList<>()).deck(new ArrayList<>())
                .discard(new ArrayList<>()).prizes(new ArrayList<>()).build();

        state = BoardState.builder()
                .matchId("m1").player1Id("p1").player2Id("p2")
                .player1Board(p1).player2Board(p2)
                .currentTurnPlayerId("p1").turnNumber(2)
                .cardCache(cache).build();
    }

    @Test
    void baseDamage_straightNumber_appliedCorrectly() {
        AttackSnapshot atk = AttackSnapshot.builder()
                .name("Thunder Shock").cost(List.of("Lightning")).convertedEnergyCost(1)
                .damage("30").build();

        // Give attacker enough energy
        attacker.getAttachedEnergies().add(CardInstance.builder()
                .instanceId("e1").cardId("lightning-basic").build());
        state.getCardCache().put("lightning-basic", energyCard("Lightning"));

        AttackContext ctx = calculator.calculate(state, "p1", atk, attacker, defender);

        assertThat(ctx.isCancelled()).isFalse();
        assertThat(ctx.getBaseDamage()).isEqualTo(30);
        assertThat(defender.getCurrentHp()).isEqualTo(30); // 60 - 30
    }

    @Test
    void weakness_doublesModifiedDamage() {
        // Geodude is weak to Grass; let's swap attacker to be Grass type
        state.getCardCache().put("xy1-1", CardSnapshot.builder()
                .cardId("xy1-1").name("Bulbasaur").supertype("POKEMON")
                .subtypes(List.of("Basic")).types(List.of("Grass"))
                .hp(45).retreatCost(1).build());

        AttackSnapshot atk = AttackSnapshot.builder()
                .name("Vine Whip").cost(List.of("Grass")).convertedEnergyCost(1)
                .damage("20").build();

        attacker.getAttachedEnergies().add(CardInstance.builder()
                .instanceId("e1").cardId("grass-basic").build());
        state.getCardCache().put("grass-basic", energyCard("Grass"));

        AttackContext ctx = calculator.calculate(state, "p1", atk, attacker, defender);

        assertThat(ctx.isCancelled()).isFalse();
        // 20 base × 2 weakness = 40
        assertThat(ctx.getModifiedDamage()).isEqualTo(40);
        assertThat(defender.getCurrentHp()).isEqualTo(20); // 60 - 40
    }

    @Test
    void resistance_reduces20() {
        // Add resistance to the defender: resists Lightning
        state.getCardCache().put("xy1-50", CardSnapshot.builder()
                .cardId("xy1-50").name("Geodude").supertype("POKEMON")
                .subtypes(List.of("Basic")).types(List.of("Fighting"))
                .hp(60).weaknessType(null)
                .resistanceType("Lightning").resistanceValue("-20").build());

        AttackSnapshot atk = AttackSnapshot.builder()
                .name("Thunderbolt").cost(List.of("Lightning")).convertedEnergyCost(1)
                .damage("60").build();

        attacker.getAttachedEnergies().add(CardInstance.builder()
                .instanceId("e1").cardId("lightning-basic").build());
        state.getCardCache().put("lightning-basic", energyCard("Lightning"));

        AttackContext ctx = calculator.calculate(state, "p1", atk, attacker, defender);

        // 60 - 20 resistance = 40
        assertThat(ctx.getModifiedDamage()).isEqualTo(40);
        assertThat(defender.getCurrentHp()).isEqualTo(20);
    }

    @Test
    void insufficientEnergy_cancelledWithMessage() {
        AttackSnapshot atk = AttackSnapshot.builder()
                .name("Thunderbolt").cost(List.of("Lightning", "Lightning")).convertedEnergyCost(2)
                .damage("90").build();

        // No energy attached
        AttackContext ctx = calculator.calculate(state, "p1", atk, attacker, defender);

        assertThat(ctx.isCancelled()).isTrue();
        assertThat(ctx.getCancelReason()).contains("Energía insuficiente");
        assertThat(defender.getCurrentHp()).isEqualTo(60); // no damage
    }

    @Test
    void knockout_flaggedWhenHpReachesZero() {
        AttackSnapshot atk = AttackSnapshot.builder()
                .name("Tackle").cost(List.of("Colorless")).convertedEnergyCost(1)
                .damage("100").build();

        attacker.getAttachedEnergies().add(CardInstance.builder()
                .instanceId("e1").cardId("lightning-basic").build());
        state.getCardCache().put("lightning-basic", energyCard("Lightning"));

        AttackContext ctx = calculator.calculate(state, "p1", atk, attacker, defender);

        assertThat(ctx.isDefenderKnockedOut()).isTrue();
        assertThat(defender.getCurrentHp()).isEqualTo(0);
    }

    @Test
    void variableDamageString_parsedAsBaseNumber() {
        AttackSnapshot atk = AttackSnapshot.builder()
                .name("Power Blast").cost(List.of("Colorless")).convertedEnergyCost(1)
                .damage("60+").build(); // variable damage

        attacker.getAttachedEnergies().add(CardInstance.builder()
                .instanceId("e1").cardId("lightning-basic").build());
        state.getCardCache().put("lightning-basic", energyCard("Lightning"));

        AttackContext ctx = calculator.calculate(state, "p1", atk, attacker, defender);
        assertThat(ctx.getBaseDamage()).isEqualTo(60);
    }

    @Test
    void paralyzed_attackIsCancelled() {
        attacker.setParalyzed(true);
        AttackSnapshot atk = AttackSnapshot.builder()
                .name("Tackle").cost(List.of("Colorless")).damage("10").build();

        attacker.getAttachedEnergies().add(CardInstance.builder()
                .instanceId("e1").cardId("colorless").build());
        state.getCardCache().put("colorless", energyCard("Colorless"));

        AttackContext ctx = calculator.calculate(state, "p1", atk, attacker, defender);
        assertThat(ctx.isCancelled()).isTrue();
        assertThat(ctx.getCancelReason()).contains("paralizado");
    }

    private CardSnapshot energyCard(String type) {
        return CardSnapshot.builder()
                .cardId(type.toLowerCase() + "-basic").name(type + " Energy")
                .supertype("ENERGY").subtypes(List.of("Basic"))
                .types(List.of(type)).basicEnergy(true).build();
    }
}

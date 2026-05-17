package com.pokemon.tcg.modules.game.engine.service;

import com.pokemon.tcg.modules.game.engine.model.PlayerBoard;
import com.pokemon.tcg.modules.game.engine.model.PokemonInPlay;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

class StatusEffectManagerTest {

    private PlayerBoard boardWithActive(PokemonInPlay active) {
        return PlayerBoard.builder()
                .playerId("p1").activePokemon(active)
                .bench(new ArrayList<>()).hand(new ArrayList<>())
                .deck(new ArrayList<>()).discard(new ArrayList<>())
                .prizes(new ArrayList<>()).build();
    }

    private PokemonInPlay freshPokemon(int hp) {
        return PokemonInPlay.builder()
                .instanceId("p1").cardId("xy1-1")
                .currentHp(hp).maxHp(hp)
                .attachedEnergies(new ArrayList<>())
                .evolutionStack(new ArrayList<>()).build();
    }

    @Test
    void poisoned_deals10DamagePerTurn() {
        StatusEffectManager mgr = new StatusEffectManager();
        PokemonInPlay poke = freshPokemon(60);
        poke.setPoisoned(true);

        mgr.applyBetweenTurns(boardWithActive(poke));

        assertThat(poke.getCurrentHp()).isEqualTo(50);
        assertThat(poke.isPoisoned()).isTrue(); // poison stays
    }

    @Test
    void burned_tails_deals20Damage() {
        // Fixed RNG always returns false (tails)
        StatusEffectManager mgr = new StatusEffectManager(new Random() {
            @Override public boolean nextBoolean() { return false; }
        });
        PokemonInPlay poke = freshPokemon(60);
        poke.setBurned(true);

        mgr.applyBetweenTurns(boardWithActive(poke));

        assertThat(poke.getCurrentHp()).isEqualTo(40);
        assertThat(poke.isBurned()).isTrue();
    }

    @Test
    void burned_heads_removeBurn() {
        // Fixed RNG always returns true (heads)
        StatusEffectManager mgr = new StatusEffectManager(new Random() {
            @Override public boolean nextBoolean() { return true; }
        });
        PokemonInPlay poke = freshPokemon(60);
        poke.setBurned(true);

        mgr.applyBetweenTurns(boardWithActive(poke));

        assertThat(poke.getCurrentHp()).isEqualTo(60); // no damage
        assertThat(poke.isBurned()).isFalse();
    }

    @Test
    void asleep_heads_wakeUp() {
        StatusEffectManager mgr = new StatusEffectManager(new Random() {
            @Override public boolean nextBoolean() { return true; }
        });
        PokemonInPlay poke = freshPokemon(60);
        poke.setAsleep(true);

        mgr.applyBetweenTurns(boardWithActive(poke));

        assertThat(poke.isAsleep()).isFalse();
    }

    @Test
    void asleep_tails_staysAsleep() {
        StatusEffectManager mgr = new StatusEffectManager(new Random() {
            @Override public boolean nextBoolean() { return false; }
        });
        PokemonInPlay poke = freshPokemon(60);
        poke.setAsleep(true);

        mgr.applyBetweenTurns(boardWithActive(poke));

        assertThat(poke.isAsleep()).isTrue();
    }

    @Test
    void paralyzed_autoRemovedEachTurn() {
        StatusEffectManager mgr = new StatusEffectManager();
        PokemonInPlay poke = freshPokemon(60);
        poke.setParalyzed(true);

        mgr.applyBetweenTurns(boardWithActive(poke));

        assertThat(poke.isParalyzed()).isFalse();
    }

    @Test
    void poisonedAndBurned_bothApplied_tails() {
        StatusEffectManager mgr = new StatusEffectManager(new Random() {
            @Override public boolean nextBoolean() { return false; } // tails for burn
        });
        PokemonInPlay poke = freshPokemon(60);
        poke.setPoisoned(true);
        poke.setBurned(true);

        mgr.applyBetweenTurns(boardWithActive(poke));

        // 60 - 10 (poison) - 20 (burn tails) = 30
        assertThat(poke.getCurrentHp()).isEqualTo(30);
    }

    @Test
    void hpCannotGoBelowZero_fromPoison() {
        StatusEffectManager mgr = new StatusEffectManager();
        PokemonInPlay poke = freshPokemon(5); // 5 HP
        poke.setPoisoned(true);

        mgr.applyBetweenTurns(boardWithActive(poke));

        assertThat(poke.getCurrentHp()).isEqualTo(0);
    }

    @Test
    void noActive_doesNotThrow() {
        StatusEffectManager mgr = new StatusEffectManager();
        PlayerBoard board = boardWithActive(null);

        assertThatNoException().isThrownBy(() -> mgr.applyBetweenTurns(board));
    }
}

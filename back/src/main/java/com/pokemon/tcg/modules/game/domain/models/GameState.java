package com.pokemon.tcg.modules.game.domain.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameState {
    public String gameId;
    public Integer turnCount;
    public String activePlayerId;
    public GamePhase currentPhase;
}

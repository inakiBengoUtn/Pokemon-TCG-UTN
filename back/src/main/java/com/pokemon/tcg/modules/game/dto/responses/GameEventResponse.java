package com.pokemon.tcg.modules.game.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameEventResponse<T> {
    private GameEventTypes type;
    private String gameId;
    private T payload;
}

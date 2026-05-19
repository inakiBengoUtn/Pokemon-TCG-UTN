package com.pokemon.tcg.modules.game.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CoinFlipResultPayload {
    private String result;
    @JsonProperty("starting_player_id")
    private String startingPlayerId;
}

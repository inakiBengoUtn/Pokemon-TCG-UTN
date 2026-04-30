package com.pokemon.tcg.modules.match.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchResponse {
    @JsonProperty("match_id")
    private String matchId;
}

package com.pokemon.tcg.modules.card.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PokemonTcgApiResponse {
    private List<PokemonTcgCardDto> data;
    private int page;
    private int pageSize;
    private int count;
    private int totalCount;
}

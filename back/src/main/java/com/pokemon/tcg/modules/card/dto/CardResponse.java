package com.pokemon.tcg.modules.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pokemon.tcg.common.enums.Supertype;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CardResponse {
    private String id;
    private String name;
    private Supertype supertype;
    private List<String> subtypes;
    private Integer hp;
    private List<String> types;
    private String evolvesFrom;
    private Integer retreatCost;
    private String weaknessType;
    private String weaknessValue;
    private String resistanceType;
    private String resistanceValue;
    private String attacksJson;
    private String abilitiesJson;
    private String imageUrlSmall;
    private String imageUrlLarge;
    private String setId;
    private String setName;
    private String number;
    private String rarity;

    @JsonProperty("isAceTactico")
    private boolean aceTactico;

    @JsonProperty("isBasicEnergy")
    private boolean basicEnergy;
}

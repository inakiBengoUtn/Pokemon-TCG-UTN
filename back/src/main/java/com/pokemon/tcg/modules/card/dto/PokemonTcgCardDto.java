package com.pokemon.tcg.modules.card.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PokemonTcgCardDto {
    private String id;
    private String name;
    private String supertype;
    private List<String> subtypes;
    private String hp;
    private List<String> types;
    private String evolvesFrom;
    private List<String> retreatCost;
    private List<AttackDto> attacks;
    private List<AbilityDto> abilities;
    private List<WeaknessResistanceDto> weaknesses;
    private List<WeaknessResistanceDto> resistances;
    private List<String> rules;
    private ImagesDto images;
    private SetDto set;
    private String number;
    private String rarity;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AttackDto {
        private String name;
        private List<String> cost;
        private int convertedEnergyCost;
        private String damage;
        private String text;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AbilityDto {
        private String name;
        private String text;
        private String type;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeaknessResistanceDto {
        private String type;
        private String value;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImagesDto {
        private String small;
        private String large;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SetDto {
        private String id;
        private String name;
    }
}

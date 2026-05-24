package com.pokemon.tcg.modules.catalog.models.card;

public enum Subtype {
    SUPPORTER,ITEM,STADIUM,POKEMON_TOOL,SPECIAL,BASIC,STAGE_1,STAGE_2,MEGA,EX;

    public static Subtype fromString(String text) {
        if (text == null) return null;
        String normalized = text.toUpperCase().replace(" ", "_");
        try {
            return Subtype.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

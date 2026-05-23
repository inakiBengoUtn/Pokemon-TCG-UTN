package com.pokemon.tcg.modules.catalog.models;

public enum Stage {
    BASIC,STAGE_1,STAGE_2,MEGA,EX;

    public static Stage fromString(String text) {
        if (text == null) return null;
        String normalized = text.toUpperCase().replace(" ", "_");
        try {
            return Stage.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

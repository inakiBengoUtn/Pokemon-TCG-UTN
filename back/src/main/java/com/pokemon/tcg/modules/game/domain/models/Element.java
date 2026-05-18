package com.pokemon.tcg.modules.game.domain.models;

import com.pokemon.tcg.modules.game.domain.models.Card.Stage;

public enum Element {
    COLORLESS,FIRE,GRASS,WATER,LIGHTNING,PSYCHIC,FIGHTING,DARKNESS,METAL,FAIRY;

    public static Element fromString(String text) {
        if (text == null) return null;
        String normalized = text.toUpperCase().replace(" ", "_");
        try {
            return Element.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

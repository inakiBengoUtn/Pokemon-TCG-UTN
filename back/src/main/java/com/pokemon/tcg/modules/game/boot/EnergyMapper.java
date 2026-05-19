package com.pokemon.tcg.modules.game.boot;

import com.fasterxml.jackson.databind.JsonNode;
import com.pokemon.tcg.modules.game.domain.models.Card.EnergyCard;
import com.pokemon.tcg.modules.game.domain.models.Card.Subtype;
import com.pokemon.tcg.modules.game.domain.models.Card.Supertype;

import java.util.ArrayList;
import java.util.List;

public class EnergyMapper {

    public static EnergyCard toEnergyCard(JsonNode node) {
        List<Subtype> subtypes = new ArrayList<>();
        JsonNode subtypesNode = node.path("subtypes");

        for (JsonNode subtype : subtypesNode) {
            Subtype s = Subtype.fromString(subtype.asText());
            if (s != null) {
                subtypes.add(s);
            }
        }

        return EnergyCard.builder()
                .id(node.path("id").asText())
                .name(node.path("name").asText())
                .supertype(Supertype.ENERGY)
                .image(node.path("images").path("small").asText())
                .subtypes(subtypes)
                .build();
    }
}

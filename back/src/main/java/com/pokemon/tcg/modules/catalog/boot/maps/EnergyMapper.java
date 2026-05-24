package com.pokemon.tcg.modules.catalog.boot.maps;

import com.fasterxml.jackson.databind.JsonNode;
import com.pokemon.tcg.modules.catalog.models.card.Element;
import com.pokemon.tcg.modules.catalog.models.card.EnergyCard;
import com.pokemon.tcg.modules.catalog.models.card.Subtype;
import com.pokemon.tcg.modules.catalog.models.card.Supertype;

import java.util.ArrayList;
import java.util.List;

public class EnergyMapper {

    public static EnergyCard toEnergyCard(JsonNode node) {
        List<Subtype> subtypes = new ArrayList<>();
        String element = "";
        JsonNode subtypesNode = node.path("subtypes");

        for (JsonNode subtype : subtypesNode) {
            Subtype s = Subtype.fromString(subtype.asText());
            if (s != null) {
                subtypes.add(s);
            }
        }

        if (subtypes.stream().anyMatch(c -> c.equals(Subtype.BASIC))) {
            element = node.path("name").asText().replace(" Energy","");
        }

        return EnergyCard.builder()
                .id(node.path("id").asText())
                .name(node.path("name").asText())
                .supertype(Supertype.ENERGY)
                .image(node.path("images").path("small").asText())
                .subtypes(subtypes)
                .element(element.isEmpty() ? null : Element.fromString(element))
                .build();
    }
}

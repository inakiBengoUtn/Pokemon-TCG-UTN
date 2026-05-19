package com.pokemon.tcg.modules.game.boot;

import com.fasterxml.jackson.databind.JsonNode;
import com.pokemon.tcg.modules.game.domain.Card.Subtype;
import com.pokemon.tcg.modules.game.domain.Card.Supertype;
import com.pokemon.tcg.modules.game.domain.Card.TrainerCard;

import java.util.ArrayList;
import java.util.List;

public class TrainerMapper {

    public static TrainerCard toTrainerCard(JsonNode node) {
        List<Subtype> subtypes = new ArrayList<>();
        JsonNode subtypesNode = node.path("subtypes");

        for (JsonNode subtype : subtypesNode) {
            Subtype s = Subtype.fromString(subtype.asText());
            if (s != null) {
                subtypes.add(s);
            }
        }

        return TrainerCard.builder()
                .id(node.path("id").asText())
                .name(node.path("name").asText())
                .supertype(Supertype.TRAINER)
                .image(node.path("images").path("small").asText())
                .subtypes(subtypes)
                .build();
    }
}

package com.pokemon.tcg.modules.game.boot;

import com.fasterxml.jackson.databind.JsonNode;
import com.pokemon.tcg.modules.game.domain.Card.Attack;
import com.pokemon.tcg.modules.game.domain.Card.PokemonCard;
import com.pokemon.tcg.modules.game.domain.Card.Subtype;
import com.pokemon.tcg.modules.game.domain.Card.Supertype;
import com.pokemon.tcg.modules.game.domain.Element;

import java.util.ArrayList;
import java.util.List;

public class PokemonMapper {

    public static PokemonCard toPokemonCard(JsonNode node) {
        Element element = Element.fromString(node.path("types").get(0).asText());
        List<Subtype> subtypes = new ArrayList<>();
        List<Attack> attacks = new ArrayList<>();
        JsonNode subtypesNode = node.path("subtypes");
        JsonNode attacksNode = node.path("attacks");
        JsonNode abilityNode = node.path("abilities").get(0);
        JsonNode weaknessesNode = node.path("weaknesses").get(0);
        JsonNode resistancesNode = node.path("resistances").get(0);

        for (JsonNode subtype : subtypesNode) {
            Subtype s = Subtype.fromString(subtype.asText());
            if (s != null) {
                subtypes.add(s);
            }
        }

        for (JsonNode attack : attacksNode) {
            Attack a = toAttack(attack);
            if (a != null) {
                attacks.add(a);
            }
        }

        return PokemonCard.builder()
                .id(node.path("id").asText())
                .name(node.path("name").asText())
                .supertype(Supertype.POKEMON)
                .image(node.path("images").path("small").asText())
                .subtypes(subtypes)
                .evolvesTo(node.path("evolvesTo").asText())
                .evolveFrom(node.path("evolvesFrom").asText())
                .hp(node.path("hp").asInt())
                .damageCounter(0)
                .element(element)
                .attacks(attacks)
                .retreatCost(node.path("convertedRetreatCost").asInt())
                .ability(abilityNode == null ? null : abilityNode.asText())
                .weaknesse(weaknessesNode == null ? null : Element.fromString(node.path("type").asText()))
                .resistance(resistancesNode == null ? null : Element.fromString(resistancesNode.path("type").asText()))
                .build();
    }

    private static Attack toAttack(JsonNode node) {
        List<Element> cost = new ArrayList<>();
        JsonNode costNode = node.path("cost");

        for (JsonNode element : costNode) {
            Element e = Element.fromString(element.asText());
            if (e != null){
                cost.add(e);
            }
        }

        return Attack.builder()
                .name(node.path("name").asText())
                .cost(cost)
                .damage(node.path("damage").asInt())
                .build();
    }
}

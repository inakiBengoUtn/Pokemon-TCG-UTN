package com.pokemon.tcg.modules.catalog.boot.maps;

import com.fasterxml.jackson.databind.JsonNode;
import com.pokemon.tcg.modules.catalog.models.card.*;

import java.util.ArrayList;
import java.util.List;

public class PokemonMapper {

    public static Card toPokemonCard(JsonNode node) {
        Element element = Element.fromString(node.path("types").get(0).asText());
        List<Subtype> subtypes = new ArrayList<>();
        List<Attack> attacks = new ArrayList<>();
        Ability ability = null;
        String evolvesTo = node.hasNonNull("evolvesTo") ? node.path("evolvesTo").get(0).asText() : null;
        String evolvesFrom = node.hasNonNull("evolvesFrom") ? node.path("evolvesFrom").asText() : null;
        JsonNode subtypesNode = node.path("subtypes");
        JsonNode attacksNode = node.path("attacks");
        JsonNode abilityNode = node.hasNonNull("abilities")
                ? node.path("abilities").get(0)
                : null;
        String weaknesses = node.hasNonNull("weaknesses")
                ? node.path("weaknesses").get(0).path("type").asText()
                : null;
        String resistances = node.hasNonNull("resistances")
                ? node.path("resistances").get(0).path("type").asText()
                : null;

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

        if (abilityNode != null) {
            ability = Ability.builder()
                    .name(abilityNode.path("name").asText())
                    .build();
        }

        PokemonCard pokemonCard = PokemonCard.builder()
                .id(node.path("id").asText())
                .name(node.path("name").asText())
                .supertype(Supertype.POKEMON)
                .image(node.path("images").path("small").asText())
                .subtypes(subtypes)
                .evolvesTo(evolvesTo)
                .evolveFrom(evolvesFrom)
                .hp(node.path("hp").asInt())
                .element(element)
                .retreatCost(node.path("convertedRetreatCost").asInt())
                .ability(ability)
                .weaknesse(Element.fromString(weaknesses))
                .resistance(Element.fromString(resistances))
                .build();

        for (Attack attack : attacks) {
            pokemonCard.addAttack(attack);
        }

        return pokemonCard;
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
        String damage = node.path("damage").asText().isEmpty() ?"0" :node.path("damage").asText();
        String damageReplaced = damage.replace("+","").replace("×","");

        return Attack.builder()
                .name(node.path("name").asText())
                .cost(cost)
                .damage(Integer.valueOf(damageReplaced))
                .build();
    }
}

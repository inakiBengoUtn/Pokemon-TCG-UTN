package com.pokemon.tcg.modules.deck.service;

import com.pokemon.tcg.common.enums.Supertype;
import com.pokemon.tcg.modules.deck.dto.response.DeckValidationResponse;
import com.pokemon.tcg.modules.deck.model.Deck;
import com.pokemon.tcg.modules.deck.model.DeckCard;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DeckValidationService {

    private static final int REQUIRED_TOTAL = 60;
    private static final int MAX_COPIES = 4;
    private static final int MAX_ACE_TACTICO = 1;

    public DeckValidationResponse validate(Deck deck) {
        List<String> errors = new ArrayList<>();
        int total = deck.totalCards();

        // Regla 1: exactamente 60 cartas
        if (total != REQUIRED_TOTAL) {
            errors.add(String.format(
                "El mazo debe tener exactamente 60 cartas. Actualmente tiene %d.", total));
        }

        // Regla 2: máximo 4 copias del mismo nombre (excepto Energía Básica)
        Map<String, Integer> countByName = deck.getCards().stream()
                .filter(dc -> !dc.getCard().isBasicEnergy())
                .collect(Collectors.groupingBy(
                        dc -> dc.getCard().getName(),
                        Collectors.summingInt(DeckCard::getQuantity)));

        countByName.forEach((name, count) -> {
            if (count > MAX_COPIES) {
                errors.add(String.format(
                    "Máximo %d copias de \"%s\" (tiene %d).", MAX_COPIES, name, count));
            }
        });

        // Regla 3: máximo 1 AS TÁCTICO en todo el mazo
        int aceTacticoCount = deck.getCards().stream()
                .filter(dc -> dc.getCard().isAceTactico())
                .mapToInt(DeckCard::getQuantity)
                .sum();
        if (aceTacticoCount > MAX_ACE_TACTICO) {
            errors.add("Solo puede haber 1 carta de AS TÁCTICO en todo el mazo.");
        }

        // Regla 4: al menos 1 Pokémon Básico
        boolean hasBasicPokemon = deck.getCards().stream()
                .anyMatch(dc -> dc.getCard().getSupertype() == Supertype.POKEMON
                        && dc.getCard().getSubtypes() != null
                        && dc.getCard().getSubtypes().stream()
                                .anyMatch(s -> s.equalsIgnoreCase("Basic")));
        if (!hasBasicPokemon) {
            errors.add("El mazo debe contener al menos 1 Pokémon Básico.");
        }

        return DeckValidationResponse.builder()
                .valid(errors.isEmpty())
                .totalCards(total)
                .errors(errors)
                .build();
    }
}

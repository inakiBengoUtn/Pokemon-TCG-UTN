package com.pokemon.tcg.modules.card.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemon.tcg.common.enums.Supertype;
import com.pokemon.tcg.modules.card.dto.CardResponse;
import com.pokemon.tcg.modules.card.dto.PokemonTcgApiResponse;
import com.pokemon.tcg.modules.card.dto.PokemonTcgCardDto;
import com.pokemon.tcg.modules.card.model.Card;
import com.pokemon.tcg.modules.card.repo.CardRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {

    private static final String API_BASE_URL = "https://api.pokemontcg.io/v2";
    private static final String XY1_SET_ID = "xy1";

    private final CardRepo cardRepo;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Descarga todas las cartas del set indicado desde pokemontcg.io y las persiste en DB.
     * Si el set ya está en caché, no hace nada (salvo que se force).
     */
    @Transactional
    public int syncSet(String setId, boolean force) {
        if (!force && cardRepo.existsBySetId(setId)) {
            log.info("Set {} already cached, skipping sync", setId);
            return 0;
        }

        String url = API_BASE_URL + "/cards?q=set.id:" + setId + "&pageSize=250";
        log.info("Fetching cards from pokemontcg.io: {}", url);

        PokemonTcgApiResponse response = restTemplate.getForObject(url, PokemonTcgApiResponse.class);
        if (response == null || response.getData() == null) {
            throw new RuntimeException("Empty response from pokemontcg.io for set: " + setId);
        }

        List<Card> cards = response.getData().stream()
                .map(dto -> mapToEntity(dto, setId))
                .toList();

        cardRepo.saveAll(cards);
        log.info("Synced {} cards for set {}", cards.size(), setId);
        return cards.size();
    }

    public List<CardResponse> searchCards(String setId, String name, String supertype) {
        List<Card> cards;

        if (supertype != null && !supertype.isBlank()) {
            Supertype supertypeEnum = Supertype.valueOf(supertype.toUpperCase());
            cards = cardRepo.findBySetIdAndSupertype(setId, supertypeEnum);
        } else if (name != null && !name.isBlank()) {
            cards = cardRepo.findBySetIdAndNameContainingIgnoreCase(setId, name);
        } else {
            cards = cardRepo.findBySetId(setId);
        }

        // Filtro adicional por nombre si hay supertype y name juntos
        if (name != null && !name.isBlank() && supertype != null && !supertype.isBlank()) {
            String lowerName = name.toLowerCase();
            cards = cards.stream()
                    .filter(c -> c.getName().toLowerCase().contains(lowerName))
                    .toList();
        }

        return cards.stream().map(this::mapToResponse).toList();
    }

    public boolean isSetCached(String setId) {
        return cardRepo.existsBySetId(setId);
    }

    private Card mapToEntity(PokemonTcgCardDto dto, String setId) {
        Supertype supertype = mapSupertype(dto.getSupertype());
        List<String> subtypes = dto.getSubtypes() != null ? dto.getSubtypes() : List.of();

        boolean aceTactico = subtypes.stream()
                .anyMatch(s -> s.equalsIgnoreCase("ACE SPEC"));

        boolean basicEnergy = supertype == Supertype.ENERGY
                && subtypes.stream().anyMatch(s -> s.equalsIgnoreCase("Basic"));

        PokemonTcgCardDto.WeaknessResistanceDto weakness = dto.getWeaknesses() != null && !dto.getWeaknesses().isEmpty()
                ? dto.getWeaknesses().get(0) : null;
        PokemonTcgCardDto.WeaknessResistanceDto resistance = dto.getResistances() != null && !dto.getResistances().isEmpty()
                ? dto.getResistances().get(0) : null;

        return Card.builder()
                .id(dto.getId())
                .name(dto.getName())
                .supertype(supertype)
                .subtypes(subtypes)
                .hp(parseHp(dto.getHp()))
                .types(dto.getTypes() != null ? dto.getTypes() : List.of())
                .evolvesFrom(dto.getEvolvesFrom())
                .retreatCost(dto.getRetreatCost() != null ? dto.getRetreatCost().size() : 0)
                .weaknessType(weakness != null ? weakness.getType() : null)
                .weaknessValue(weakness != null ? weakness.getValue() : null)
                .resistanceType(resistance != null ? resistance.getType() : null)
                .resistanceValue(resistance != null ? resistance.getValue() : null)
                .attacksJson(toJson(dto.getAttacks()))
                .abilitiesJson(toJson(dto.getAbilities()))
                .rules(dto.getRules() != null ? dto.getRules() : List.of())
                .imageUrlSmall(dto.getImages() != null ? dto.getImages().getSmall() : null)
                .imageUrlLarge(dto.getImages() != null ? dto.getImages().getLarge() : null)
                .setId(setId)
                .setName(dto.getSet() != null ? dto.getSet().getName() : null)
                .number(dto.getNumber())
                .rarity(dto.getRarity())
                .aceTactico(aceTactico)
                .basicEnergy(basicEnergy)
                .build();
    }

    private CardResponse mapToResponse(Card card) {
        return CardResponse.builder()
                .id(card.getId())
                .name(card.getName())
                .supertype(card.getSupertype())
                .subtypes(card.getSubtypes())
                .hp(card.getHp())
                .types(card.getTypes())
                .evolvesFrom(card.getEvolvesFrom())
                .retreatCost(card.getRetreatCost())
                .weaknessType(card.getWeaknessType())
                .weaknessValue(card.getWeaknessValue())
                .resistanceType(card.getResistanceType())
                .resistanceValue(card.getResistanceValue())
                .attacksJson(card.getAttacksJson())
                .abilitiesJson(card.getAbilitiesJson())
                .imageUrlSmall(card.getImageUrlSmall())
                .imageUrlLarge(card.getImageUrlLarge())
                .setId(card.getSetId())
                .setName(card.getSetName())
                .number(card.getNumber())
                .rarity(card.getRarity())
                .aceTactico(card.isAceTactico())
                .basicEnergy(card.isBasicEnergy())
                .build();
    }

    private Supertype mapSupertype(String apiSupertype) {
        if (apiSupertype == null) return Supertype.TRAINER;
        return switch (apiSupertype.toLowerCase()) {
            case "pokémon", "pokemon" -> Supertype.POKEMON;
            case "energy" -> Supertype.ENERGY;
            default -> Supertype.TRAINER;
        };
    }

    private Integer parseHp(String hp) {
        if (hp == null || hp.isBlank()) return null;
        try { return Integer.parseInt(hp); } catch (NumberFormatException e) { return null; }
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        try { return objectMapper.writeValueAsString(obj); }
        catch (JsonProcessingException e) { return null; }
    }
}

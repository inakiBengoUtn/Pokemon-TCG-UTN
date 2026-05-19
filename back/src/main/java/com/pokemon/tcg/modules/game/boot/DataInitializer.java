package com.pokemon.tcg.modules.game.boot;

import com.fasterxml.jackson.databind.JsonNode;
import com.pokemon.tcg.modules.game.domain.Card.EnergyCard;
import com.pokemon.tcg.modules.game.domain.Card.PokemonCard;
import com.pokemon.tcg.modules.game.domain.Card.TrainerCard;
import com.pokemon.tcg.modules.game.repositories.EnergyCardRepo;
import com.pokemon.tcg.modules.game.repositories.PokemonCardRepo;
import com.pokemon.tcg.modules.game.repositories.TrainerCardRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private static final String URL_API = "https://api.pokemontcg.io/v2/cards?q=set.id:xy1";
    private final RestClient restClient;
    private final PokemonCardRepo pokemonRepo;
    private final TrainerCardRepo trainerRepo;
    private final EnergyCardRepo energyRepo;

    public DataInitializer(RestClient.Builder builder,
                           PokemonCardRepo pokemonRepo,
                           TrainerCardRepo trainerRepo,
                           EnergyCardRepo energyRepo) {
        this.restClient = builder.baseUrl(URL_API).build();
        this.pokemonRepo = pokemonRepo;
        this.trainerRepo = trainerRepo;
        this.energyRepo = energyRepo;
    }

    @Override
    public void run(String ...args) {
        log.info("Iniciando pre-carga de cartas de Pokémon en Redis...");
        try {
            cacheAllCards();
            log.info("Caché cargada exitosamente.");
        } catch (Exception e) {
            log.error("Error cargando la caché inicial: " + e.getMessage());
            // Aquí decides si detener la app o continuar sin caché
        }
    }

    public void cacheAllCards() {
        JsonNode root = restClient.get().retrieve().body(JsonNode.class);
        assert root != null;
        List<PokemonCard> pokemonCards = mapToPokemonCard(root);
        List<TrainerCard> trainerCards = mapToTrainerCard(root);
        List<EnergyCard> energyCards = mapToEnergyCard(root);
        pokemonRepo.saveAll(pokemonCards);
        trainerRepo.saveAll(trainerCards);
        energyRepo.saveAll(energyCards);
    }

    private List<PokemonCard> mapToPokemonCard(JsonNode root) {
        List<PokemonCard> cards = new ArrayList<>();
        JsonNode cardsArray = root.path("data");
        for (JsonNode node : cardsArray) {
            String supertype = node.path("supertype").asText();

            if (supertype.equals("Pokémon")) {
                cards.add(PokemonMapper.toPokemonCard(node));
            }
        }

        return cards;
    }

    private List<TrainerCard> mapToTrainerCard(JsonNode root) {
        List<TrainerCard> cards = new ArrayList<>();
        JsonNode cardsArray = root.path("data");
        for (JsonNode node : cardsArray) {
            String supertype = node.path("supertype").asText();

            if (supertype.equals("Trainer")) {
                cards.add(TrainerMapper.toTrainerCard(node));
            }
        }

        return cards;
    }

    private List<EnergyCard> mapToEnergyCard(JsonNode root) {
        List<EnergyCard> cards = new ArrayList<>();
        JsonNode cardsArray = root.path("data");
        for (JsonNode node : cardsArray) {
            String supertype = node.path("supertype").asText();

            if (supertype.equals("Energy")) {
                cards.add(EnergyMapper.toEnergyCard(node));
            }
        }

        return cards;
    }
}

package com.pokemon.tcg.modules.catalog.boot;

import com.fasterxml.jackson.databind.JsonNode;
import com.pokemon.tcg.modules.catalog.boot.maps.EnergyMapper;
import com.pokemon.tcg.modules.catalog.boot.maps.PokemonMapper;
import com.pokemon.tcg.modules.catalog.boot.maps.TrainerMapper;
import com.pokemon.tcg.modules.catalog.exceptions.ApiDataException;
import com.pokemon.tcg.modules.catalog.models.card.Card;
import com.pokemon.tcg.modules.catalog.repo.CardRepo;
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
    private final CardRepo cardRepo;

    public DataInitializer(RestClient.Builder builder, CardRepo cardRepo) {
        this.restClient = builder.baseUrl(URL_API).build();
        this.cardRepo = cardRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Iniciando pre-carga de cartas de Pokémon en PostgresSQL...");
        try {
            cacheAllCards();
            log.info("Caché cargada exitosamente.");
        } catch (Exception e) {
            log.error("Error cargando la caché inicial: " + e.getMessage());
            throw new RuntimeException(e);
            // Aquí decides si detener la app o continuar sin caché
        }
    }

    private void cacheAllCards() {
        JsonNode node = restClient.get().retrieve().body(JsonNode.class);

        if (node == null) {
            throw new ApiDataException();
        }

        List<Card> cards = mapToCard(node);
        cardRepo.saveAll(cards);
    }

    private List<Card> mapToCard(JsonNode root) {
        List<Card> cards = new ArrayList<>();
        JsonNode cardsArray = root.path("data");

        for (JsonNode node : cardsArray) {
            String supertype = node.path("supertype").asText();

            if (supertype.equals("Pokémon")) {
                cards.add(PokemonMapper.toPokemonCard(node));
            } else if (supertype.equals("Trainer")) {
                cards.add(TrainerMapper.toTrainerCard(node));
            } else if (supertype.equals("Energy")) {
                cards.add(EnergyMapper.toEnergyCard(node));
            }
        }

        return cards;
    }
}

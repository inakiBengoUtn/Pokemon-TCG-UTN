package com.pokemon.tcg.modules.game;

import com.fasterxml.jackson.databind.JsonNode;
import com.pokemon.tcg.modules.game.domain.models.Card.PokemonCard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import javax.smartcardio.Card;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private static final String URL_API = "https://api.pokemontcg.io/v2/cards?q=set.id:xy1";
    private RestClient restClient;

    public DataInitializer(RestClient.Builder builder) {
        this.restClient = builder.baseUrl(URL_API).build();
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
        JsonNode jsonNode = restClient.get().retrieve().body(JsonNode.class);
        assert jsonNode != null;
        mapToPokemonCard(jsonNode);
    }

    private void mapToPokemonCard(JsonNode jsonNode) {
        System.out.println(jsonNode.toString());
    }
}

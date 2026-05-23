package com.pokemon.tcg.modules.deck.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemon.tcg.modules.catalog.repositories.PokemonCardRepo;
import com.pokemon.tcg.modules.catalog.models.PokemonCard;
import com.pokemon.tcg.modules.deck.dto.responses.DeckResponse;
import com.pokemon.tcg.modules.deck.models.Deck;
import com.pokemon.tcg.modules.deck.models.DeckCard;
import com.pokemon.tcg.modules.deck.repo.DeckRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DeckService {
    private final DeckRepo deckRepo;
    private final ObjectMapper objectMapper;
    private final PokemonCardRepo pokemonCardRepo;

    public List<DeckResponse> getAllDecks(String username) {
        List<Deck> userDecks = deckRepo.findDecksByUsername(username);
        List<DeckResponse> decksResponse = new ArrayList<>();
        for (Deck userDeck : userDecks) {
            decksResponse.add(mapToDeckResponse(userDeck));
        }
        return decksResponse;
    }

    private DeckResponse mapToDeckResponse(Deck deck) {
        DeckResponse deckResponse = new DeckResponse();
        // extraemos el primer deckCard que sea pokemon del mazo
        DeckCard card = deck.getCards().getFirst();
        String cardId = card.getCard().getId();

        // buscamos la carta en redis
        PokemonCard pokemonCard = pokemonCardRepo.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Not found pokemon "+cardId+" in redis." ));

        deckResponse.setCoverImage(pokemonCard.getImage());
        deckResponse.setName(deck.getName());
        deckResponse.setId(deck.getId());
        return deckResponse;
    }
}

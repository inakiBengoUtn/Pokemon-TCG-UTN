package com.pokemon.tcg.modules.catalog.services;

import com.pokemon.tcg.modules.catalog.dto.responses.CardResponse;
import com.pokemon.tcg.modules.catalog.dto.responses.DeckResponse;
import com.pokemon.tcg.modules.catalog.dto.responses.DeckSummaryResponse;
import com.pokemon.tcg.modules.catalog.exceptions.DeckNotFounException;
import com.pokemon.tcg.modules.catalog.models.Deck;
import com.pokemon.tcg.modules.catalog.models.DeckCard;
import com.pokemon.tcg.modules.catalog.models.card.Card;
import com.pokemon.tcg.modules.catalog.repo.DeckRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DeckService {
    private final DeckRepo deckRepo;

    public List<DeckSummaryResponse> getAllDecks(String username) {
        List<Deck> userDecks = deckRepo.findDecksByUsername(username);
        List<DeckSummaryResponse> decksResponse = new ArrayList<>();
        for (Deck userDeck : userDecks) {
            decksResponse.add(mapToDeckSummaryResponse(userDeck));
        }
        return decksResponse;
    }

    public DeckResponse getDeck(String id) {
        UUID deckId = UUID.fromString(id);
        Deck deck = deckRepo.findById(deckId)
                .orElseThrow(() -> new DeckNotFounException(deckId));
        return mapToDeckResponse(deck);
    }

    private DeckResponse mapToDeckResponse(Deck deck) {
        DeckResponse deckResponse = new DeckResponse();
        deckResponse.setId(deck.getId());
        deckResponse.setQuantity(deck.getCards().toArray().length);
        deckResponse.setName(deck.getName());
        String image = deck.getCards().getFirst().getCard().getImage();
        deckResponse.setCoverImage(image);

        List<CardResponse> cardResponses = new ArrayList<>();
        for (DeckCard deckCard : deck.getCards()) {
            cardResponses.add(mapToCardResponse(deckCard));
        }

        deckResponse.setCards(cardResponses);
        return deckResponse;
    }

    private CardResponse mapToCardResponse(DeckCard deckCard) {
        CardResponse cardResponse = new CardResponse();
        Card card = deckCard.getCard();
        cardResponse.setId(card.getId());
        cardResponse.setName(card.getName());
        cardResponse.setQuantity(deckCard.getQuantity());
        cardResponse.setImage(card.getImage());
        return cardResponse;
    }

    private DeckSummaryResponse mapToDeckSummaryResponse(Deck deck) {
        DeckSummaryResponse deckSummaryResponse = new DeckSummaryResponse();
        deckSummaryResponse.setId(deck.getId());
        deckSummaryResponse.setName(deck.getName());
        deckSummaryResponse.setQuantity(deck.getCards().toArray().length);
        String image = deck.getCards().getFirst().getCard().getImage();
        deckSummaryResponse.setCoverImage(image);
        return deckSummaryResponse;
    }
}

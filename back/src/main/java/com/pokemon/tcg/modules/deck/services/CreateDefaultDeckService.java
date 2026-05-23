package com.pokemon.tcg.modules.deck.services;

import com.pokemon.tcg.modules.deck.models.Card;
import com.pokemon.tcg.modules.deck.models.Deck;
import com.pokemon.tcg.modules.deck.models.DeckCard;
import com.pokemon.tcg.modules.deck.repo.DeckRepo;
import com.pokemon.tcg.modules.user.models.User;
import com.pokemon.tcg.modules.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreateDefaultDeckService {
    private final UserRepo userRepo;
    private final DeckRepo deckRepo;
    private final Map<String, Integer> defaultDeck = Map.ofEntries(
            Map.entry("xy1-96", 3), //pokemon
            Map.entry("xy1-97", 2), //pokemon
            Map.entry("xy1-92", 3), //pokemon
            Map.entry("xy1-93", 3), //pokemon
            Map.entry("xy1-91", 2), //pokemon
            Map.entry("xy1-87", 1), //pokemon
            Map.entry("xy1-88", 1), //pokemon
            Map.entry("xy1-89", 1), //pokemon
            Map.entry("xy1-90", 1), //pokemon
            Map.entry("xy1-122", 4), //trainer
            Map.entry("xy1-127", 4), //trainer
            Map.entry("xy1-125", 4), //trainer
            Map.entry("xy1-118", 4), //trainer
            Map.entry("xy1-116", 3), //trainer
            Map.entry("xy1-123", 3), //trainer
            Map.entry("xy1-117", 2), //trainer
            Map.entry("xy1-121", 2), //trainer
            Map.entry("xy1-128", 2), //trainer
            Map.entry("xy1-120", 1), //trainer
            Map.entry("xy1-115", 1), //trainer
            Map.entry("xy1-140", 10), //energy
            Map.entry("xy1-130", 4) //energy
    );

    public void createDefaultDeck(User user) {
        Deck deck = new Deck();
        List<DeckCard> deckCards = createDefaultDeckCard(deck);

        deck.setUser(user);
        deck.setCards(deckCards);
        deck.setName("Default deck");

        user.setActiveDeck(deck);
        deckRepo.save(deck);
        userRepo.save(user);
    }

    private List<DeckCard> createDefaultDeckCard(Deck deck) {
        return defaultDeck.entrySet()
                .stream()
                .map(entry -> {
                    DeckCard deckCard = new DeckCard();
                    Card card = new Card();
                    card.setId(entry.getKey());
                    deckCard.setCard(card);
                    deckCard.setQuantity(entry.getValue());
                    deckCard.setDeck(deck);
                    return deckCard;
                })
                .toList();
    }

}

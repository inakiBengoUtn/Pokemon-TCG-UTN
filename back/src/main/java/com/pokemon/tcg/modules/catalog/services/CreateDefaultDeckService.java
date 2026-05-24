package com.pokemon.tcg.modules.catalog.services;

import com.pokemon.tcg.modules.catalog.models.Deck;
import com.pokemon.tcg.modules.catalog.models.DeckCard;
import com.pokemon.tcg.modules.catalog.models.card.Card;
import com.pokemon.tcg.modules.catalog.repo.CardRepo;
import com.pokemon.tcg.modules.catalog.repo.DeckRepo;
import com.pokemon.tcg.modules.user.models.User;
import com.pokemon.tcg.modules.user.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreateDefaultDeckService {
    private final UserRepo userRepo;
    private final DeckRepo deckRepo;
    private final CardRepo cardRepo;
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

    @Transactional // <- ocurre en una transaccion
    public void createDefaultDeck(User user) {
        Deck deck = new Deck();
        deck.setUser(user);
        deck.setName("Default deck");

        List<Card> cards = cardRepo.findAllById(defaultDeck.keySet());

        for (Card card : cards) {
            Integer quantity = defaultDeck.get(card.getId());
            deck.addCard(card, quantity);
        }

        user.setActiveDeck(deck);

        deckRepo.save(deck);
        userRepo.save(user);
    }

}

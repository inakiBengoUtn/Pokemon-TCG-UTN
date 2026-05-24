package com.pokemon.tcg.modules.catalog.models;


import com.pokemon.tcg.modules.catalog.models.card.Card;
import com.pokemon.tcg.modules.user.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "decks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Deck {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeckCard> cards = new ArrayList<>();

    public void addCard(Card card, Integer quantity) {
        DeckCard deckCard = new DeckCard();
        deckCard.setCard(card);
        deckCard.setQuantity(quantity);
        this.cards.add(deckCard);
    }
}

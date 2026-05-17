package com.pokemon.tcg.modules.deck.model;

import com.pokemon.tcg.modules.card.model.Card;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "deck_cards",
    uniqueConstraints = @UniqueConstraint(columnNames = {"deck_id", "card_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeckCard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    // Máximo 4 (excepto Energía Básica sin límite, y AS TÁCTICO máximo 1)
    @Column(nullable = false)
    private int quantity;
}

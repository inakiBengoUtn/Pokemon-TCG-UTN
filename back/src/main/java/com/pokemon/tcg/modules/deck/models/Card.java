package com.pokemon.tcg.modules.deck.models;

import com.pokemon.tcg.modules.catalog.models.Supertype;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private Supertype supertype;
}

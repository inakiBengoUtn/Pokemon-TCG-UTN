package com.pokemon.tcg.modules.catalog.models.card;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "card_type", discriminatorType = DiscriminatorType.STRING)
@SuperBuilder
public abstract class Card {
    @Id
    private String id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Supertype supertype;
    private String image;
}

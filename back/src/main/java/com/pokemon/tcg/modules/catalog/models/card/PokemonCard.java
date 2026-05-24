package com.pokemon.tcg.modules.catalog.models.card;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pokemon_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("POKEMON")
@SuperBuilder
public class PokemonCard extends Card {
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "subtypes", columnDefinition = "jsonb")
    private List<Subtype> subtypes = new ArrayList<>();
    private String evolvesTo;
    private String evolveFrom;
    private Integer hp;
    @Enumerated(EnumType.STRING)
    private Element element;
    @Builder.Default
    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attack> attacks = new ArrayList<>();
    private Integer retreatCost;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ability_id")
    private Ability ability;
    @Enumerated(EnumType.STRING)
    private Element weaknesse;
    @Enumerated(EnumType.STRING)
    private Element resistance;

    public void addAttack(Attack attack) {
        attack.setCard(this);
        this.attacks.add(attack);
    }
}

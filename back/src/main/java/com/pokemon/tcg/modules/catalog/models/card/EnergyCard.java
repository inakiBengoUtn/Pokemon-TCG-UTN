package com.pokemon.tcg.modules.catalog.models.card;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Table(name = "energy_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("ENERGY")
@SuperBuilder
public class EnergyCard extends Card {
    @Enumerated(EnumType.STRING)
    private Element element;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "subtypes", columnDefinition = "jsonb")
    private List<Subtype> subtypes;
}

package com.pokemon.tcg.modules.catalog.models.card;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trainer_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("TRAINER")
@SuperBuilder
public class TrainerCard extends Card {
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "instructions", columnDefinition = "jsonb")
    private JsonNode instructions;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "subtypes", columnDefinition = "jsonb")
    private List<Subtype> subtypes = new ArrayList<>();
}

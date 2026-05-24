package com.pokemon.tcg.modules.catalog.models.card;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "attacks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "cost", columnDefinition = "jsonb")
    private List<Element> cost = new ArrayList<>();

    private Integer damage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "instructions", columnDefinition = "jsonb")
    private JsonNode instructions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private PokemonCard card;
}

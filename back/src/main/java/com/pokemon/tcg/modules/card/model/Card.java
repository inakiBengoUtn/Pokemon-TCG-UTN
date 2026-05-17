package com.pokemon.tcg.modules.card.model;

import com.pokemon.tcg.common.enums.Supertype;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    // ID proveniente de la API pokemontcg.io (ej: "xy1-1")
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Supertype supertype;

    // Subtypes: Basic, Stage 1, Stage 2, Item, Supporter, Stadium, Tool, EX, Mega, etc.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "card_subtypes", joinColumns = @JoinColumn(name = "card_id"))
    @Column(name = "subtype")
    private List<String> subtypes;

    // Solo para POKEMON
    private Integer hp;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "card_types", joinColumns = @JoinColumn(name = "card_id"))
    @Column(name = "type")
    private List<String> types;

    private String evolvesFrom;

    // Cantidad de Energía incolora que cuesta retirarse
    private Integer retreatCost;

    // Debilidad (ej: "Fire", "×2")
    private String weaknessType;
    private String weaknessValue;

    // Resistencia (ej: "Water", "-20")
    private String resistanceType;
    private String resistanceValue;

    // JSON de ataques: [{name, cost:[], convertedEnergyCost, damage, text}]
    @Column(columnDefinition = "TEXT")
    private String attacksJson;

    // JSON de habilidades: [{name, text, type}]
    @Column(columnDefinition = "TEXT")
    private String abilitiesJson;

    // Reglas especiales de la carta (ej: "When 1 of your Pokémon-EX is Knocked Out...")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "card_rules", joinColumns = @JoinColumn(name = "card_id"))
    @Column(name = "rule", columnDefinition = "TEXT")
    private List<String> rules;

    private String imageUrlSmall;
    private String imageUrlLarge;

    @Column(nullable = false)
    private String setId;

    private String setName;
    private String number;
    private String rarity;

    // Solo puede haber 1 AS TÁCTICO en todo el mazo
    @Column(nullable = false)
    private boolean aceTactico;

    // true = Energía Básica (sin límite de copias en el mazo)
    @Column(nullable = false)
    private boolean basicEnergy;
}

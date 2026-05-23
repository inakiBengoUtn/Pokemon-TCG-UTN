package com.pokemon.tcg.modules.catalog.models;

import com.redis.om.spring.annotations.Document;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Document("energy")
@SuperBuilder
@Getter
@Setter
public class EnergyCard extends Card {
    private List<Subtype> subtypes;
}

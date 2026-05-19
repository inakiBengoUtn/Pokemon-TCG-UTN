package com.pokemon.tcg.modules.game.domain.Card;

import com.redis.om.spring.annotations.Document;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Document("trainer") // esta es la key
@Getter
@Setter
public class TrainerCard extends Card {
    private List<Subtype> subtypes;
}

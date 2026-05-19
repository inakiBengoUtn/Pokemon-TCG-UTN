package com.pokemon.tcg.modules.game.domain.Card;

import com.pokemon.tcg.modules.game.domain.Element;
import com.redis.om.spring.annotations.Document;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Document("pokemon") // esta es la key
@Getter
@Setter
public class PokemonCard extends Card {
    private List<Subtype> subtypes;
    private String evolvesTo;
    private String evolveFrom;
    private Integer hp;
    private Integer damageCounter;
    private Element element;
    private List<Attack> attacks;
    private Integer retreatCost;
    private String ability;
    private Element weaknesse;
    private Element resistance;
}

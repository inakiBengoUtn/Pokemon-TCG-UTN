package com.pokemon.tcg.modules.game.domain.models.Card;

import com.pokemon.tcg.modules.game.domain.models.Element;
import com.pokemon.tcg.modules.game.domain.models.Stage;

import java.util.List;

public class PokemonCard extends Card {
    private Stage Stage;
    private String evolvesTo;
    private String evolveFrom;
    private Integer hp;
    private Integer damageCounter;
    private Element elementType;
    private List<Attack> attacks;
    private Integer retreatCost;
    private List<Ability> ability;
    private Element weaknesse;
    private Element resistances;
}

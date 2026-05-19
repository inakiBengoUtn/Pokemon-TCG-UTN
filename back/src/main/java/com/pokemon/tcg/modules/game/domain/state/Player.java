package com.pokemon.tcg.modules.game.domain.state;

import com.pokemon.tcg.modules.game.domain.Card.Card;
import com.pokemon.tcg.modules.game.domain.Card.PokemonCard;
import lombok.Builder;

import java.util.List;

@Builder
public class Player {
    private String name;
    private List<Card> hand;
    private List<Card> deck;
    private List<Card> prizeDeck;
    private List<Card> discardDeck;
    private List<PokemonCard> bench;
    private List<PokemonCard> active;
}

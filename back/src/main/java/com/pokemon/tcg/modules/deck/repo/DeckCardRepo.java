package com.pokemon.tcg.modules.deck.repo;

import com.pokemon.tcg.modules.deck.model.DeckCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeckCardRepo extends JpaRepository<DeckCard, UUID> {
    List<DeckCard> findByDeckId(UUID deckId);
}

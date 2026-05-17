package com.pokemon.tcg.modules.deck.repo;

import com.pokemon.tcg.modules.deck.model.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeckRepo extends JpaRepository<Deck, UUID> {

    List<Deck> findByOwnerId(UUID userId);
}

package com.pokemon.tcg.modules.deck.repo;

import com.pokemon.tcg.modules.deck.models.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeckRepo extends JpaRepository<Deck, UUID> {
    @Query("""
            SELECT d FROM Deck d
            JOIN d.user u
            WHERE u.username = :username
            """)
    List<Deck> findDecksByUsername(@Param("username")String username);
}

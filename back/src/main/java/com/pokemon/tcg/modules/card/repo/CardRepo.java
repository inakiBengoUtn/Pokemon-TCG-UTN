package com.pokemon.tcg.modules.card.repo;

import com.pokemon.tcg.modules.card.model.Card;
import com.pokemon.tcg.common.enums.Supertype;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepo extends JpaRepository<Card, String> {

    List<Card> findBySetId(String setId);

    List<Card> findBySetIdAndSupertype(String setId, Supertype supertype);

    @Query("SELECT c FROM Card c WHERE c.setId = :setId AND LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Card> findBySetIdAndNameContainingIgnoreCase(String setId, String name);

    boolean existsBySetId(String setId);
}

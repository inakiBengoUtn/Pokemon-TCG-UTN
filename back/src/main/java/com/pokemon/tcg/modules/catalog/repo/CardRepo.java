package com.pokemon.tcg.modules.catalog.repo;

import com.pokemon.tcg.modules.catalog.models.card.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepo extends JpaRepository<Card, String> {
}

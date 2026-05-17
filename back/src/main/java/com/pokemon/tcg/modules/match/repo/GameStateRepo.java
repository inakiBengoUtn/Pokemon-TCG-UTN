package com.pokemon.tcg.modules.match.repo;

import com.pokemon.tcg.modules.match.model.GameState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameStateRepo extends JpaRepository<GameState, UUID> {

    Optional<GameState> findByMatchId(UUID matchId);
}

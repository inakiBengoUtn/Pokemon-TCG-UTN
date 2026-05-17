package com.pokemon.tcg.modules.match.repo;

import com.pokemon.tcg.common.enums.MatchStatus;
import com.pokemon.tcg.modules.match.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MatchRepo extends JpaRepository<Match, UUID> {

    List<Match> findByStatus(MatchStatus status);

    List<Match> findByPlayer1IdOrPlayer2Id(UUID player1Id, UUID player2Id);
}

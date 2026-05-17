package com.pokemon.tcg.modules.match.repo;

import com.pokemon.tcg.modules.match.model.ActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActionLogRepo extends JpaRepository<ActionLog, UUID> {

    List<ActionLog> findByMatchIdOrderByTimestampAsc(UUID matchId);

    List<ActionLog> findByMatchIdAndTurnNumberOrderByTimestampAsc(UUID matchId, int turnNumber);
}

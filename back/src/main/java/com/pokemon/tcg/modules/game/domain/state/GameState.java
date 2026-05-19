package com.pokemon.tcg.modules.game.domain.state;

import com.pokemon.tcg.modules.game.domain.GamePhase;
import com.redis.om.spring.annotations.Document;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document("game")
@Builder
public class GameState {
    @Id
    private String gameId;
    private Integer turnCount;
    private String activePlayerId;
    private GamePhase currentPhase;
    private Player player1;
    private Player player2;
}

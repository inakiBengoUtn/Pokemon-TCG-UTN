package com.pokemon.tcg.modules.game.repositories;

import com.pokemon.tcg.modules.game.domain.state.GameState;
import com.redis.om.spring.repository.RedisDocumentRepository;

public interface GameStateRepo extends RedisDocumentRepository<GameState, String> {
}

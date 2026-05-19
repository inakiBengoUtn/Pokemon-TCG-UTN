package com.pokemon.tcg.modules.game.repositories;

import com.pokemon.tcg.modules.game.domain.Card.PokemonCard;
import com.redis.om.spring.repository.RedisDocumentRepository;
import org.springframework.stereotype.Repository;

public interface PokemonCardRepo extends RedisDocumentRepository<PokemonCard, String> {
}

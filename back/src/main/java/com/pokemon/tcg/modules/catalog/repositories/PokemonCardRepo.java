package com.pokemon.tcg.modules.catalog.repositories;

import com.pokemon.tcg.modules.catalog.models.PokemonCard;
import com.redis.om.spring.repository.RedisDocumentRepository;

import java.util.Optional;

public interface PokemonCardRepo extends RedisDocumentRepository<PokemonCard, String> {
}

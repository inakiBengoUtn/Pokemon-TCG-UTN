package com.pokemon.tcg.modules.game.repositories;

import com.pokemon.tcg.modules.game.domain.Card.EnergyCard;
import com.redis.om.spring.repository.RedisDocumentRepository;

public interface EnergyCardRepo extends RedisDocumentRepository<EnergyCard,String> {
}

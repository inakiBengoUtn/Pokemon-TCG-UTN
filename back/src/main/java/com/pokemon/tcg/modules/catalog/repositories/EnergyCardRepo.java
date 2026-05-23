package com.pokemon.tcg.modules.catalog.repositories;

import com.pokemon.tcg.modules.catalog.models.EnergyCard;
import com.redis.om.spring.repository.RedisDocumentRepository;

public interface EnergyCardRepo extends RedisDocumentRepository<EnergyCard,String> {
}

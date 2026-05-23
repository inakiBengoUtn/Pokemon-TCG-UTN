package com.pokemon.tcg.modules.catalog.repositories;

import com.pokemon.tcg.modules.catalog.models.TrainerCard;
import com.redis.om.spring.repository.RedisDocumentRepository;

public interface TrainerCardRepo extends RedisDocumentRepository<TrainerCard, String> {
}

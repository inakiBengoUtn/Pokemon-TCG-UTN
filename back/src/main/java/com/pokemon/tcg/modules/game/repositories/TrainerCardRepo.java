package com.pokemon.tcg.modules.game.repositories;

import com.pokemon.tcg.modules.game.domain.Card.TrainerCard;
import com.redis.om.spring.repository.RedisDocumentRepository;

public interface TrainerCardRepo extends RedisDocumentRepository<TrainerCard, String> {
}

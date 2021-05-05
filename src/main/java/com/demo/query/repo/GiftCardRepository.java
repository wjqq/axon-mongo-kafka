package com.demo.query.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.demo.query.GiftCardEntity;

public interface GiftCardRepository extends MongoRepository<GiftCardEntity, String> {
}

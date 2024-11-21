package com.screenvault.screenvaultAPI.shortKey;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface ShortKeyRepository extends MongoRepository<ShortKey, String> {
    boolean existsByKey(String Key);
}

package com.screenvault.screenvaultAPI.collection;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CollectionRepository extends MongoRepository<Collection, UUID> {
    @Query(fields = "{ 'posts': 0, 'ownerUsername': 0 }")
    Optional<List<Collection>> findAllByOwnerUsername(String ownerUsername);
}

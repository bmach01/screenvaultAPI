package com.screenvault.screenvaultAPI.collection;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CollectionRepository extends MongoRepository<Collection, UUID> {
    Optional<List<Collection>> findAllByOwnerUsername(String ownerUsername);
}

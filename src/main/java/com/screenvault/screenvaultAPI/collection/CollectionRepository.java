package com.screenvault.screenvaultAPI.collection;

import com.mongodb.lang.Nullable;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends MongoRepository<Collection, ObjectId> {
    Optional<List<Collection>> findAllByOwnerUsername(String ownerUsername);

    Optional<Collection> findByIsGlobal(boolean isGlobal);
}

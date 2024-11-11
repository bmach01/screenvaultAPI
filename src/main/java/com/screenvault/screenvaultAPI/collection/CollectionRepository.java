package com.screenvault.screenvaultAPI.collection;

import com.mongodb.lang.Nullable;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CollectionRepository extends MongoRepository<Collection, ObjectId> {

    @Nullable
    List<Collection> findAllByOwnerUsername(String ownerUsername);

    @Nullable
    Collection findByIsGlobal(boolean isGlobal);

}

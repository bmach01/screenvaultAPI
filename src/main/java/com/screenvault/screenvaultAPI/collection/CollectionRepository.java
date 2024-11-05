package com.screenvault.screenvaultAPI.collection;

import com.mongodb.lang.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface CollectionRepository extends MongoRepository<Collection, UUID> {

    @Nullable
    Page<Collection> findAllByOwnerId(UUID ownerId, Pageable pageable);

    @Nullable
    Collection findByIsGlobal(boolean isGlobal);

}

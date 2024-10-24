package com.screenvault.screenvaultAPI.collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface CollectionRepository extends MongoRepository<Collection, UUID> {

    Page<Collection> findAllByOwnerId(UUID ownerId, Pageable pageable);

}

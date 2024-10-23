package com.screenvault.screenvaultAPI.collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface CollectionRepository extends PagingAndSortingRepository<Collection, UUID> {

    Page<Collection> findAllByUserId(UUID ownerId, Pageable pageable);

}

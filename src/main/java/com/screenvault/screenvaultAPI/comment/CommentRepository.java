package com.screenvault.screenvaultAPI.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends MongoRepository<Comment, UUID> {
    Optional<Page<Comment>> findByIdIn(List<UUID> commentIds, Pageable pageable);
    void deleteByIdIn(List<UUID> commentIds);

    @Query("{ 'reports': { $gt: 0 } }")
    Optional<Page<Comment>> findByReportsGreaterThanZero(Pageable pageable);
}

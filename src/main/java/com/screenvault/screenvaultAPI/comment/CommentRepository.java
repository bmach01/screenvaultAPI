package com.screenvault.screenvaultAPI.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends PagingAndSortingRepository<Comment, UUID>, MongoRepository<Comment, UUID> {
    @Query(value = "{ 'reportCount': { $gt: 0 }, 'isDeleted': { $ne: true } }",
            fields = "{ 'reportCount': 0, 'isDeleted': 0 }")
    Optional<Page<Comment>> findByReportsGreaterThanZero(Pageable pageable);

    @Query(value = "{ 'postId': ?0, 'isDeleted': { $ne: true } }",
            fields = "{ 'reportCount': 0, 'isDeleted': 0 }")
    Optional<Page<Comment>> findAllByPostId(UUID postId, Pageable pageable);
}

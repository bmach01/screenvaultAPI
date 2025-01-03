package com.screenvault.screenvaultAPI.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface CommentRepository extends PagingAndSortingRepository<Comment, UUID>, CommentCustomRepository, MongoRepository<Comment, UUID> {
    @Query(value = "{ 'postId': ?0, 'isDeleted': { $ne: true } }",
            fields = "{ 'reportCount': 0, 'isDeleted': 0, 'isVerified': 0 }",
            sort = "{ 'postedOn': -1 }")
    Page<Comment> findByPostId(UUID postId, Pageable pageable);
}

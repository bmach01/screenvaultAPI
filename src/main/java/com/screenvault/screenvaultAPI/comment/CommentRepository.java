package com.screenvault.screenvaultAPI.comment;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends MongoRepository<Comment, UUID> {

    List<Comment> findAllById(UUID postId);

}

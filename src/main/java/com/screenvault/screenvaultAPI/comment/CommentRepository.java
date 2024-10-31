package com.screenvault.screenvaultAPI.comment;

import com.mongodb.lang.Nullable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends MongoRepository<Comment, UUID> {

    @Nullable
    List<Comment> findAllById(UUID postId);

}

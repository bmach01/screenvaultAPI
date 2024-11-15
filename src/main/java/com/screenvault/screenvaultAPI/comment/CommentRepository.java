package com.screenvault.screenvaultAPI.comment;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends MongoRepository<Comment, ObjectId> {
    Optional<Page<Comment>> findByIdIn(List<ObjectId> commentIds, Pageable pageable);
}

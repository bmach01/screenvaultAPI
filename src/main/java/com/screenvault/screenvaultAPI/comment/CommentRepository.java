package com.screenvault.screenvaultAPI.comment;

import com.mongodb.lang.Nullable;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, ObjectId> {

    @Nullable
    Page<Comment> findByIdIn(List<ObjectId> commentIds, Pageable pageable);

}

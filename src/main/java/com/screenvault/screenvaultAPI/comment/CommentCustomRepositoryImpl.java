package com.screenvault.screenvaultAPI.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.UUID;

public class CommentCustomRepositoryImpl implements CommentCustomRepository {

    private final MongoTemplate mongoTemplate;

    public CommentCustomRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;

        mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
    }

    @Override
    public Page<Comment> findReported(Pageable pageable) {
        Query query = new Query();
        query.addCriteria(Criteria.where("reportCount").gt(0).and("isDeleted").ne(true));
        query.with(Sort.by(Sort.Direction.DESC, "reportCount"));
        query.with(pageable);
        List<Comment> comments = mongoTemplate.find(query, Comment.class);
        long total = mongoTemplate.count(Query.query(Criteria.where("reportCount").gt(0)), Comment.class);

        return new PageImpl<>(comments, pageable, total);
    }

    @Override
    public void markDeleteByPostId(UUID postId) {
        Query query = new Query(Criteria.where("postId").is(postId));
        Update update = new Update().set("isDeleted", true);
        mongoTemplate.updateMulti(query, update, Comment.class);
    }
}

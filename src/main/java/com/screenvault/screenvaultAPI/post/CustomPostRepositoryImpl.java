package com.screenvault.screenvaultAPI.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.UUID;

public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final MongoTemplate mongoTemplate;

    public CustomPostRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<Post> findReported(Pageable pageable) {
        Query query = new Query();
        query.addCriteria(Criteria.where("reportCount").gt(0));
        query.with(Sort.by(Sort.Direction.DESC, "reportCount"));
        query.with(pageable);
        List<Post> posts = mongoTemplate.find(query, Post.class);
        long total = mongoTemplate.count(Query.query(Criteria.where("reportCount").gt(0)), Post.class);

        return new PageImpl<>(posts, pageable, total);
    }

    @Override
    public void incrementViewCount(UUID postId) {
        Query query = new Query(Criteria.where("_id").is(postId));
        Update update = new Update().inc("viewCount", 1);
        mongoTemplate.updateFirst(query, update, Post.class);
    }

    @Override
    public void incrementReportCount(UUID postId) {
        Query query = new Query(Criteria.where("_id").is(postId));
        Update update = new Update().inc("reportCount", 1);
        mongoTemplate.updateFirst(query, update, Post.class);
    }

    @Override
    public void incrementCommentCount(UUID postId) {
        Query query = new Query(Criteria.where("_id").is(postId));
        Update update = new Update().inc("commentCount", 1);
        mongoTemplate.updateFirst(query, update, Post.class);
    }

    @Override
    public void decrementCommentCount(UUID postId) {
        Query query = new Query(Criteria.where("_id").is(postId));
        Update update = new Update().inc("commentCount", -1);
        mongoTemplate.updateFirst(query, update, Post.class);
    }

    @Override
    public void removeCollectionFromPosts(UUID collectionId) {
        Query query = new Query(Criteria.where("collectionIds").is(collectionId));
        Update update = new Update().pull("collectionIds", collectionId);
        mongoTemplate.updateMulti(query, update, Post.class);
    }
}

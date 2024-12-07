package com.screenvault.screenvaultAPI.post;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PostAsyncService {

    private final MongoTemplate mongoTemplate;

    public PostAsyncService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Async
    public void incrementViewCountAndSave(UUID postId) {
        try {
            Query query = new Query(Criteria.where("_id").is(postId));
            Update update = new Update().inc("viewCount", 1);
            mongoTemplate.updateFirst(query, update, Post.class);
        }
        catch (RuntimeException ignore) {}
    }

    @Async
    public void incrementReportCountAndSave(UUID postId) {
        try {
            Query query = new Query(Criteria.where("_id").is(postId));
            Update update = new Update().inc("reportCount", 1);
            mongoTemplate.updateFirst(query, update, Post.class);
        }
        catch (RuntimeException ignore) {}
    }

    @Async
    public void incrementCommentCountAndSave(UUID postId) {
        try {
            Query query = new Query(Criteria.where("_id").is(postId));
            Update update = new Update().inc("commentCount", 1);
            mongoTemplate.updateFirst(query, update, Post.class);
        }
        catch (RuntimeException ignore) {}
    }

    @Async
    public void decrementCommentCountAndSave(UUID postId) {
        try {
            Query query = new Query(Criteria.where("_id").is(postId));
            Update update = new Update().inc("commentCount", -1);
            mongoTemplate.updateFirst(query, update, Post.class);
        }
        catch (RuntimeException ignore) {}
    }

    @Async
    public void removeCollectionFromPosts(UUID collectionId) {
        try {
            Query query = new Query(Criteria.where("collectionIds").is(collectionId));
            Update update = new Update().pull("collectionIds", collectionId);
            mongoTemplate.updateMulti(query, update, Post.class);
        }
        catch (RuntimeException ignore) {}
    }
}

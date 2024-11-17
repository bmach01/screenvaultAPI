package com.screenvault.screenvaultAPI.collection;

import com.screenvault.screenvaultAPI.jwt.JwtService;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final JwtService jwtService;
    private final MongoTemplate mongoTemplate;


    public CollectionService(CollectionRepository collectionRepository, JwtService jwtService, MongoTemplate mongoTemplate) {
        this.collectionRepository = collectionRepository;
        this.jwtService = jwtService;
        this.mongoTemplate = mongoTemplate;
    }

    public List<Collection> getMyCollections(String token) {
        String username = jwtService.extractUsername(token);
        return collectionRepository.findAllByOwnerUsername(username).orElse(Collections.emptyList());
    }

    public Collection addPostToMyCollection(String token, UUID postId, UUID collectionId)
            throws PermissionDeniedDataAccessException, InternalError, IllegalArgumentException, NoSuchElementException {
        Collection collection = collectionRepository.findById(collectionId).orElseThrow();

        if (!collection.getOwnerUsername().equals(jwtService.extractUsername(token)))
            throw new PermissionDeniedDataAccessException("Collection is not principal's.", null);

        if (!addPostToCollection(postId, collectionId))
            throw new InternalError("Failed to update the collection. Try again later");

        return collection;
    }

    public Collection uploadCollection(String token, Collection collection)
            throws IllegalArgumentException, OptimisticLockingFailureException {
        Collection savedCollection = null;
        collection.setGlobal(false);
        collection.setOwnerUsername(jwtService.extractUsername(token));

        try {
            savedCollection = collectionRepository.save(collection);

        } catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }

        return savedCollection;
    }

    public boolean addPostToCollection(UUID postId, UUID collectionId) {
        Query query = new Query(Criteria.where("id").is(collectionId));
        Update update = new Update().addToSet("posts", postId);
        return mongoTemplate.updateFirst(query, update, Collection.class).getModifiedCount() != 0;
    }

}

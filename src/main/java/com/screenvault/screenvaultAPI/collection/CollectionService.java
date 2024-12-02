package com.screenvault.screenvaultAPI.collection;

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
    private final MongoTemplate mongoTemplate;


    public CollectionService(CollectionRepository collectionRepository, MongoTemplate mongoTemplate) {
        this.collectionRepository = collectionRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<Collection> getMyCollections(String username) {
        return collectionRepository.findAllByOwnerUsername(username).orElse(Collections.emptyList());
    }

    public Collection addPostToMyCollection(String username, UUID postId, UUID collectionId)
            throws PermissionDeniedDataAccessException, IllegalArgumentException, NoSuchElementException
    {
        Collection collection = collectionRepository.findById(collectionId).orElseThrow();

        if (!collection.getOwnerUsername().equals(username))
            throw new PermissionDeniedDataAccessException("Collection is not principal's.", null);

        if (!addPostToCollection(postId, collectionId))
            throw new NoSuchElementException("Failed to update the collection. Isn't this post already in the collection?");

        return collection;
    }

    public Collection removePostFromMyCollection(String username, UUID postId, UUID collectionId)
            throws PermissionDeniedDataAccessException, IllegalArgumentException, NoSuchElementException
    {
        Collection collection = collectionRepository.findById(collectionId).orElseThrow();

        if (!collection.getOwnerUsername().equals(username))
            throw new PermissionDeniedDataAccessException("Collection is not principal's.", null);

        if (!removePostFromCollection(postId, collectionId))
            throw new NoSuchElementException("Failed to update the collection. Was this post really in the collection?");

        return collection;
    }

    private boolean addPostToCollection(UUID postId, UUID collectionId) {
        Query query = new Query(Criteria.where("id").is(collectionId));
        Update update = new Update().addToSet("posts", postId);
        return mongoTemplate.updateFirst(query, update, Collection.class).getModifiedCount() != 0;
    }

    private boolean removePostFromCollection(UUID postId, UUID collectionId) {
        Query query = new Query(Criteria.where("id").is(collectionId));
        Update update = new Update().pull("posts", postId);
        return mongoTemplate.updateFirst(query, update, Collection.class).getModifiedCount() != 0;
    }

    public Collection uploadCollection(String username, Collection collection)
            throws IllegalArgumentException, OptimisticLockingFailureException
    {
        Collection savedCollection = null;
        collection.setOwnerUsername(username);

        try {
            savedCollection = collectionRepository.save(collection);
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }

        return savedCollection;
    }

    public void deleteCollection(String username, UUID collectionId)
        throws IllegalArgumentException, NoSuchElementException
    {
        try {
            Collection collection = collectionRepository.findById(collectionId).orElseThrow();
            if (!collection.getOwnerUsername().equals(username))
                throw new PermissionDeniedDataAccessException("Collection is not principal's.", null);

            collectionRepository.deleteById(collection.getId());
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }
}

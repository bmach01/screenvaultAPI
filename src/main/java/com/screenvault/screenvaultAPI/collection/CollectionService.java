package com.screenvault.screenvaultAPI.collection;

import com.screenvault.screenvaultAPI.jwt.JwtService;
import com.screenvault.screenvaultAPI.post.PostService;
import org.bson.types.ObjectId;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final JwtService jwtService;
    private final PostService postService;

    public CollectionService(CollectionRepository collectionRepository, JwtService jwtService, PostService postService) {
        this.collectionRepository = collectionRepository;
        this.jwtService = jwtService;
        this.postService = postService;
    }

    public List<Collection> getMyCollections(String token) {
        String username = jwtService.extractUsername(token);
        return collectionRepository.findAllByOwnerUsername(username).orElse(Collections.emptyList());
    }

    public Collection addPostToMyCollection(String token, ObjectId postId, ObjectId collectionId)
            throws PermissionDeniedDataAccessException, InternalError, IllegalArgumentException, NoSuchElementException {
        Collection collection = collectionRepository.findById(collectionId).orElseThrow();

        if (!collection.getOwnerUsername().equals(jwtService.extractUsername(token)))
            throw new PermissionDeniedDataAccessException("Collection is not principal's.", null);

        if (!postService.addPostToCollection(postId, collectionId))
            throw new InternalError("Failed to update the collection. Try again later");

        return collection;
    }


}

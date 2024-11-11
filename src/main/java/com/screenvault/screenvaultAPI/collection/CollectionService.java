package com.screenvault.screenvaultAPI.collection;

import com.screenvault.screenvaultAPI.jwt.JwtService;
import com.screenvault.screenvaultAPI.post.PostService;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return collectionRepository.findAllByOwnerUsername(username);
    }

    public boolean addPostToMyCollection(String token, ObjectId postId, ObjectId collectionId) {
        String username = jwtService.extractUsername(token);
        Collection collection = collectionRepository.findById(collectionId).orElse(null); // Can't make it nullable(?)

        if (collection == null) return false;
        if (!collection.getOwnerUsername().equals(username)) return false;

        postService.addPostToCollection(postId, collectionId);
        return true;
    }


}

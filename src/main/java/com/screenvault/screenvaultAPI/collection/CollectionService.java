package com.screenvault.screenvaultAPI.collection;

import com.screenvault.screenvaultAPI.jwt.JwtService;
import com.screenvault.screenvaultAPI.post.PostService;
import org.apache.coyote.BadRequestException;
import org.bson.types.ObjectId;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.security.authentication.BadCredentialsException;
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
        return collectionRepository.findAllByOwnerUsername(username).orElse(null);
    }

    public Collection addPostToMyCollection(String token, ObjectId postId, ObjectId collectionId)
            throws BadRequestException, PermissionDeniedDataAccessException, InternalError
    {
        String username = jwtService.extractUsername(token);
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new BadRequestException("Collection not found."));

        if (!collection.getOwnerUsername().equals(username))
            throw new PermissionDeniedDataAccessException("Collection is not principal's.", null);

        if (!postService.addPostToCollection(postId, collectionId))
            throw new InternalError("Failed to update the collection.");

        return collection;
    }


}

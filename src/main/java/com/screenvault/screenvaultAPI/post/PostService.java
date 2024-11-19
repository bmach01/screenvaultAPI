package com.screenvault.screenvaultAPI.post;

import com.screenvault.screenvaultAPI.jwt.JwtService;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PostService {

    private final PostRepository postRepository;
        private final ImageRepository imageRepository;
    private final JwtService jwtService;

    public PostService(
            PostRepository postRepository,
            ImageRepository imageRepository,
            JwtService jwtService
    ) {
        this.postRepository = postRepository;
        this.imageRepository = imageRepository;
        this.jwtService = jwtService;
    }

    public Page<Post> getLandingPagePostsPage(int page, int pageSize) {
        return postRepository.findAllByIsPublic(true, PageRequest.of(page, pageSize)).orElse(Page.empty());
    }

    public Page<Post> getPostsByTitle(String title, int page, int pageSize) {
        return postRepository.findByIsPublicAndTitleContaining(true, title, PageRequest.of(page, pageSize)).orElse(Page.empty());
    }

    public Page<Post> getPostsByTags(Set<String> tags, int page, int pageSize) {
        return postRepository.findByIsPublicAndTagsIn(true, tags, PageRequest.of(page, pageSize)).orElse(Page.empty());
    }

    public Post uploadPost(String token, Post post, boolean isPublic) throws InternalError, IllegalArgumentException {
        post.setPublic(isPublic);
        post.setPosterUsername(jwtService.extractUsername(token));

        Post savedPost = null;

        try {
            savedPost = postRepository.save(post);
        } catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }

        return savedPost;
    }
}

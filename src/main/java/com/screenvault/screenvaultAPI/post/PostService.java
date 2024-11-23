package com.screenvault.screenvaultAPI.post;

import com.screenvault.screenvaultAPI.jwt.JwtService;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Service
public class PostService {

    private static final String[] VALID_IMAGE_TYPES = {"image/jpeg", "image/png"};

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

    private boolean isValidImageType(String type) {
        for (String valid : VALID_IMAGE_TYPES) if (type.equals(valid)) return true;
        return false;
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

    public Post uploadPost(String token, Post post, boolean isPublic, MultipartFile image)
            throws InternalError, IllegalArgumentException {
        if (image == null) throw new IllegalArgumentException("Image must not be null.");
        if (image.isEmpty()) throw new IllegalArgumentException("Image must not be empty.");
        if (!isValidImageType(image.getContentType()))
            throw new IllegalArgumentException("Content type not supported.");

        post.setPublic(isPublic);
        post.setPosterUsername(jwtService.extractUsername(token));
        post.setPostedOn(new Date());

        Post savedPost = null;

        try {
            if (isPublic)
                imageRepository.uploadPublicImage(image, post.getId().toString());
            else
                imageRepository.uploadPrivateImage(image, post.getId().toString());

            savedPost = postRepository.save(post);
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }

        return savedPost;
    }

    public void deletePost(String token, UUID postId)
            throws IllegalArgumentException, PermissionDeniedDataAccessException, InternalError {
        String username = jwtService.extractUsername(token);

        try {
            Post post = postRepository.findById(postId).orElseThrow();
            if (!post.getPosterUsername().equals(username))
                throw new PermissionDeniedDataAccessException("Collection is not principal's.", null);

            postRepository.deleteById(postId);
        }
        catch (NullPointerException ignored) {
        } // TODO: reconsider this
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }

    }

    public Post changePostVisiblity(String token, UUID postId, boolean toPublic)
            throws IllegalArgumentException, PermissionDeniedDataAccessException, InternalError {
        String username = jwtService.extractUsername(token);
        Post post = null;
        try {
            post = postRepository.findById(postId).orElseThrow();
            if (!post.getPosterUsername().equals(username))
                throw new PermissionDeniedDataAccessException("Collection is not principal's.", null);

            post.setPublic(toPublic);
            if (toPublic) imageRepository.upateImageToPublic(post.getId().toString());
            else imageRepository.updateImageToPrivate(post.getId().toString());

            postRepository.save(post);
        }
        catch (NullPointerException ignored) {
        } // TODO: reconsider this
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }

        return post;
    }
}

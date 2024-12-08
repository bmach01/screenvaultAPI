package com.screenvault.screenvaultAPI.post;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Service
public class PostService {

    private static final String[] VALID_IMAGE_TYPES = { "image/jpeg", "image/png", "image/svg+xml", "image/webp" };

    private final PostRepository postRepository;
    private final ImageService imageService;
    private final PostAsyncService postAsyncService;

    public PostService(
            PostRepository postRepository,
            ImageService imageService,
            PostAsyncService postAsyncService
    ) {
        this.postRepository = postRepository;
        this.imageService = imageService;
        this.postAsyncService = postAsyncService;
    }

    public Page<Post> getLandingPagePostsPage(int page, int pageSize) {
        Page<Post> posts = postRepository.findAllByIsPublic(true, PageRequest.of(page, pageSize));
        posts.getContent().forEach((it) -> {
            it.setImageUrl(getImageUrlForPost(it));
        });

        return posts;
    }

    public Page<Post> getPostsByTitle(String title, int page, int pageSize) {
        Page<Post> posts = postRepository.findAllByIsPublicAndTitleContaining(true, title, PageRequest.of(page, pageSize));
        posts.getContent().forEach((it) -> {
            it.setImageUrl(getImageUrlForPost(it));
        });

        return posts;
    }

    public Page<Post> getPostsByTags(Set<String> tags, int page, int pageSize) {
        Page<Post> posts = postRepository.findAllByIsPublicAndTagsIn(true, tags, PageRequest.of(page, pageSize));
        posts.getContent().forEach((it) -> {
            it.setImageUrl(getImageUrlForPost(it));
        });

        return posts;
    }

    public Post getPostById(UUID postId) throws NoSuchElementException {
        Post post = postRepository.findById(postId).orElseThrow();
        post.setImageUrl(getImageUrlForPost(post));
        postAsyncService.incrementViewCountAndSave(postId);

        return post;
    }

    public Page<Post> getPostsByCollectionId(UUID collectionId, int page, int pageSize) {
        Page<Post> posts = postRepository.findAllByCollectionId(collectionId, PageRequest.of(page, pageSize));
        posts.getContent().forEach((it) -> {
            it.setImageUrl(getImageUrlForPost(it));
        });

        return posts;
    }

    public Post uploadPost(String username, Post post, MultipartFile image)
            throws InternalError, IllegalArgumentException
    {
        if (image == null) throw new IllegalArgumentException("Image must not be null.");
        if (post.getTitle().isBlank()) throw new IllegalArgumentException("Title must not be blank.");
        if (image.isEmpty()) throw new IllegalArgumentException("Image must not be empty.");
        if (!isValidImageType(image.getContentType()))
            throw new IllegalArgumentException("Image type not supported.");

        Post savedPost = null;

        try {
            if (post.isPublic()) {
                imageService.uploadPublicImage(image, post.getId().toString());
            }
            else {
                imageService.uploadPrivateImage(image, post.getId().toString());
            }

            post.setPosterUsername(username);
            savedPost = postRepository.save(post);
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }

        savedPost.setImageUrl(getImageUrlForPost(savedPost));
        return savedPost;
    }

    public void userMarkPostDeleted(String username, UUID postId)
            throws IllegalArgumentException, PermissionDeniedDataAccessException, InternalError, NoSuchElementException
    {
        try {
            Post post = postRepository.findById(postId).orElseThrow();
            if (!post.getPosterUsername().equals(username))
                throw new PermissionDeniedDataAccessException("Post is not principal's.", null);

            post.setDeleted(true);
            postRepository.save(post);
            postAsyncService.markDeletedCommentsForPost(postId);
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }

    public Post changePostVisibility(String username, UUID postId, boolean toPublic)
            throws IllegalArgumentException, PermissionDeniedDataAccessException, InternalError, NoSuchElementException
    {
        Post post = null;

        try {
            post = postRepository.findById(postId).orElseThrow();
            if (post.isPublic() == toPublic) return post;

            if (!post.getPosterUsername().equals(username))
                throw new PermissionDeniedDataAccessException("Post is not principal's.", null);

            if (toPublic) imageService.moveImageToPublic(post.getId().toString());
            else imageService.moveImageToPrivate(post.getId().toString());

            post.setPublic(toPublic);
            postRepository.save(post);
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }

        post.setImageUrl(getImageUrlForPost(post));
        return post;
    }

    private boolean isValidImageType(String type) {
        for (String valid : VALID_IMAGE_TYPES) if (type.equals(valid)) return true;
        return false;
    }

    private String getImageUrlForPost(Post post) throws InternalError {
        if (post.isPublic()) return imageService.getPublicImageUrl(post.getId().toString());
        return imageService.getPrivateImageUrl(post.getId().toString());
    }
}

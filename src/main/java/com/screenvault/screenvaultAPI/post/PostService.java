package com.screenvault.screenvaultAPI.post;

import com.screenvault.screenvaultAPI.comment.CommentRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class PostService {

    private static final String[] VALID_IMAGE_TYPES = { "image/jpeg", "image/png", "image/svg+xml", "image/webp" };

    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final CommentRepository commentRepository;
    private final PostAsyncService postAsyncService;

    public PostService(
            PostRepository postRepository,
            ImageRepository imageRepository,
            CommentRepository commentRepository,
            PostAsyncService postAsyncService
    ) {
        this.postRepository = postRepository;
        this.imageRepository = imageRepository;
        this.commentRepository = commentRepository;
        this.postAsyncService = postAsyncService;
    }

    private boolean isValidImageType(String type) {
        for (String valid : VALID_IMAGE_TYPES) if (type.equals(valid)) return true;
        return false;
    }

    public Page<Post> getLandingPagePostsPage(int page, int pageSize) {
        Page<Post> posts = postRepository.findAllByIsPublic(true, PageRequest.of(page, pageSize)).orElse(Page.empty());
        posts.getContent().forEach((it) -> {
            it.setImageUrl(getImageUrlForPost(it));
        });

        return posts;
    }

    public Page<Post> getPostsByTitle(String title, int page, int pageSize) {
        Page<Post> posts = postRepository.findByIsPublicAndTitleContaining(true, title, PageRequest.of(page, pageSize)).orElse(Page.empty());
        posts.getContent().forEach((it) -> {
            it.setImageUrl(getImageUrlForPost(it));
        });

        return posts;
    }

    public Page<Post> getPostsByTags(Set<String> tags, int page, int pageSize) {
        Page<Post> posts = postRepository.findByIsPublicAndTagsIn(true, tags, PageRequest.of(page, pageSize)).orElse(Page.empty());
        posts.getContent().forEach((it) -> {
            it.setImageUrl(getImageUrlForPost(it));
        });

        return posts;
    }

    public Post getPostById(UUID postId) throws NoSuchElementException {
        Post post = postRepository.findById(postId).orElseThrow();
        post.setImageUrl(getImageUrlForPost(post));
        postAsyncService.incrementViewCountAndSave(post);

        return post;
    }

    public Page<Post> getPostsByIds(Page<UUID> postIds) {
        List<Post> posts = postRepository.findByIdIn(postIds.getContent());
        posts.forEach((it) -> {
            it.setImageUrl(getImageUrlForPost(it));
        });

        return new PageImpl<>(posts, postIds.getPageable(), postIds.getTotalElements());
    }

    public Post uploadPost(String username, Post post, MultipartFile image)
            throws InternalError, IllegalArgumentException
    {
        if (image == null) throw new IllegalArgumentException("Image must not be null.");
        if (image.isEmpty()) throw new IllegalArgumentException("Image must not be empty.");
        if (!isValidImageType(image.getContentType()))
            throw new IllegalArgumentException("Image type not supported.");

        post.setPosterUsername(username);
        post.setPostedOn(new Date());
        post.setComments(Collections.emptyList());
        post.setTags(Collections.emptySet());
        post.setReportCount(0);

        Post savedPost = null;
        try {
            if (post.isPublic())
                imageRepository.uploadPublicImage(image, post.getId().toString());
            else
                imageRepository.uploadPrivateImage(image, post.getId().toString());

            savedPost = postRepository.save(post);
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }

        savedPost.setImageUrl(getImageUrlForPost(savedPost));
        return savedPost;
    }

    public void deletePost(String username, UUID postId)
            throws IllegalArgumentException, PermissionDeniedDataAccessException, InternalError, NoSuchElementException
    {
        try {
            Post post = postRepository.findById(postId).orElseThrow();
            if (!post.getPosterUsername().equals(username))
                throw new PermissionDeniedDataAccessException("Post is not principal's.", null);

            imageRepository.deleteImage(post.getId().toString(), post.isPublic());

            commentRepository.deleteByIdIn(post.getComments());
            postRepository.deleteById(postId);
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

            if (toPublic) imageRepository.moveImageToPublic(post.getId().toString());
            else imageRepository.moveImageToPrivate(post.getId().toString());

            post.setPublic(toPublic);
            postRepository.save(post);
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }

        post.setImageUrl(getImageUrlForPost(post));
        return post;
    }

    private String getImageUrlForPost(Post post) throws InternalError {
        if (post.isPublic()) return imageRepository.getPublicImageUrl(post.getId().toString());
        return imageRepository.getPrivateImageUrl(post.getId().toString());
    }
}

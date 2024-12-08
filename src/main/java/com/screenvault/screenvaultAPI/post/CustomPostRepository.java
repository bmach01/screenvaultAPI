package com.screenvault.screenvaultAPI.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomPostRepository {
    Page<Post> findReported(Pageable pageable);
    void incrementViewCount(UUID postId);
    void incrementReportCount(UUID postId);
    void incrementCommentCount(UUID postId);
    void decrementCommentCount(UUID postId);
    void removeCollectionFromPosts(UUID collectionId);
}

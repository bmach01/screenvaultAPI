package com.screenvault.screenvaultAPI.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomCommentRepository {
    Page<Comment> findReported(Pageable pageable);
    void markDeleteByPostId(UUID postId);
}

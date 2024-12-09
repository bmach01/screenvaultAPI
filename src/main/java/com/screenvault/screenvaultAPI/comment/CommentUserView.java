package com.screenvault.screenvaultAPI.comment;

import com.screenvault.screenvaultAPI.image.ImageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Date;
import java.util.UUID;

public record CommentUserView(
        UUID id,
        UUID postId,
        String username,
        String userPfpUrl,
        String text,
        Date postedOn
) {
    public CommentUserView(Comment comment) {
        this(
            comment.getId(),
            comment.getPostId(),
            comment.getUsername(),
            ImageService.getPublicImageUrl(comment.getUsername()),
            comment.getText(),
            comment.getPostedOn()
        );
    }

    public static Page<CommentUserView> mapPage(Page<Comment> comments) {
        return new PageImpl<>(comments.map(CommentUserView::new).stream().toList(), comments.getPageable(), comments.getTotalElements());
    }
}

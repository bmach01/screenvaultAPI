package com.screenvault.screenvaultAPI.post;

import com.screenvault.screenvaultAPI.rating.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

public record PostUserView(
        UUID id,
        String title,
        String posterUsername,
        String imageUrl,
        int score,
        int commentCount,
        int viewCount,
        Date postedOn,
        Set<String> tags,
        Rating.Score myScore
) {
    public PostUserView(Post post) {
        this(
            post.getId(),
            post.getTitle(),
            post.getPosterUsername(),
            post.getImageUrl(),
            post.getScore(),
            post.getCommentCount(),
            post.getViewCount(),
            post.getPostedOn(),
            post.getTags(),
            post.getMyScore()
        );
    }

    public static Page<PostUserView> mapPage(Page<Post> posts) {
        return new PageImpl<>(posts.map(PostUserView::new).stream().toList(), posts.getPageable(), posts.getTotalElements());
    }
}

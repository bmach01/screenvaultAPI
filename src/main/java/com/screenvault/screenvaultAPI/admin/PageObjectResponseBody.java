package com.screenvault.screenvaultAPI.admin;

import com.screenvault.screenvaultAPI.comment.Comment;
import com.screenvault.screenvaultAPI.post.Post;
import org.springframework.data.domain.Page;

public record PageObjectResponseBody(
        String message,
        boolean success,
        Page<?> objects
) {
}

package com.screenvault.screenvaultAPI.post;

import java.util.UUID;

public record UpdatePostVisibilityRequestBody(
        UUID postId,
        boolean toPublic
) {
}

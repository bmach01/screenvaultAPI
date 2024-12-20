package com.screenvault.screenvaultAPI.moderation;

import java.util.Set;

public record VerificationAndTagsResponseBody(
        String message,
        boolean success,
        Boolean flagged,
        Set<String> tags
) {
}

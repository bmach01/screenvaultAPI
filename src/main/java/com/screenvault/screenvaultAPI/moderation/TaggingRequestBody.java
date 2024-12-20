package com.screenvault.screenvaultAPI.moderation;

import java.util.List;

public record TaggingRequestBody(
        String model,
        int max_tokens,
        List<Message> messages
) {
    public record Message(
            String role,
            List<ModerationRequestBody.Input> content
    ) {}
}

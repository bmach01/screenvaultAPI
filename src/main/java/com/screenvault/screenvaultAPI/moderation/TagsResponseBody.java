package com.screenvault.screenvaultAPI.moderation;

import java.util.List;

public record TagsResponseBody(
        String id,
        String object,
        long created,
        String model,
        List<Choice> choices,
        Usage usage,
        String system_fingerprint
) {
    public record Choice(
            int index,
            Message message,
            String logprobs,
            String finish_reason
    ) {}

    public record Message(
            String role,
            String content,
            String refusal
    ) {}

    public record Usage(
            int prompt_tokens,
            int completion_tokens,
            int total_tokens,
            TokensDetails prompt_tokens_details,
            TokensDetails completion_tokens_details
    ) {}

    public record TokensDetails(
            int cached_tokens,
            int audio_tokens,
            int accepted_prediction_tokens,
            int rejected_prediction_tokens
    ) {}
}
package com.screenvault.screenvaultAPI.moderation;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ModerationRequestBody(
        String model,
        List<Input> input
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Input(
        String type,
        String text,
        ImageUrl image_url
    ) {
        public record ImageUrl(String url) {}
    }
}

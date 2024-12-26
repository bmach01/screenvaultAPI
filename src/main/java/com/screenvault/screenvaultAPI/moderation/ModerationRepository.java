package com.screenvault.screenvaultAPI.moderation;

public interface ModerationRepository {
    ModerationResponseBody getImageModerationResponse(String imageB64) throws InternalError, IllegalArgumentException;
    TagsResponseBody getImageTags(String image) throws InternalError, IllegalArgumentException;
}

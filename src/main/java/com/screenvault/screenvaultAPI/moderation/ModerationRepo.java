package com.screenvault.screenvaultAPI.moderation;

public interface ModerationRepo {
    public ModerationResponseBody getImageModerationResponse(String imageUrl) throws InternalError, IllegalArgumentException;
}

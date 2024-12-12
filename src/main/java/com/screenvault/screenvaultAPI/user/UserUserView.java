package com.screenvault.screenvaultAPI.user;

import com.screenvault.screenvaultAPI.image.ImageService;

public record UserUserView(
        String username,
        UserRole role,
        String profilePictureUrl
) {
    public UserUserView(User user) {
        this(user.getUsername(), user.getRole(), ImageService.getPublicImageUrl(user.getUsername()));
    }
}

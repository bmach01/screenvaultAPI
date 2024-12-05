package com.screenvault.screenvaultAPI.user;

public record ChangePasswordRequestBody(
        String oldPassword,
        String newPassword
) {
}

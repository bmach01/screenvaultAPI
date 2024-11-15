package com.screenvault.screenvaultAPI.authentication;

public record TokensDTO(
        String token,
        String refreshToken
) {
}

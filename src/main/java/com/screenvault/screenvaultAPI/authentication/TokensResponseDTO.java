package com.screenvault.screenvaultAPI.authentication;

public record TokensResponseDTO(
        String token,
        String refreshToken
) {
}

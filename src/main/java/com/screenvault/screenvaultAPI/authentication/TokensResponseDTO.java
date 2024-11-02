package com.screenvault.screenvaultAPI.authentication;

public record TokensResponseDTO(
    String message,
    String token,
    String refreshToken
) {}

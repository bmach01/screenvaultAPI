package com.screenvault.screenvaultAPI.authentication;

public record LoginResponseDTO(
    String message,
    String token,
    String refreshToken
) {}

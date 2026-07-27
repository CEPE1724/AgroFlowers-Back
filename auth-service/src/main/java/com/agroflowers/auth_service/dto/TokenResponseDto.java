package com.agroflowers.auth_service.dto;

public record TokenResponseDto(
        String accessToken,
        String refreshToken,
        Long expiresIn,
        String tokenType
) {
}

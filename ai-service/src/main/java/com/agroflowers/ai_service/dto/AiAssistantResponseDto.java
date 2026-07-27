package com.agroflowers.ai_service.dto;

public record AiAssistantResponseDto(
        String content,
        boolean isWarning
) {
}

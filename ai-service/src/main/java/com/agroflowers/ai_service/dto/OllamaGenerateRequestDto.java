package com.agroflowers.ai_service.dto;

public record OllamaGenerateRequestDto(
        String model,
        String prompt,
        String system,
        boolean stream
) {
}

package com.agroflowers.ai_service.dto;

import java.util.List;

public record GeminiGenerateRequestDto(
        SystemInstruction systemInstruction,
        List<Content> contents,
        GenerationConfig generationConfig
) {
    public record SystemInstruction(List<Part> parts) {
    }

    public record Content(List<Part> parts) {
    }

    public record Part(String text) {
    }

    public record GenerationConfig(Double temperature, Integer maxOutputTokens) {
    }
}

package com.agroflowers.ai_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OllamaGenerateResponseDto(
        String model,
        String response,
        boolean done
) {
}

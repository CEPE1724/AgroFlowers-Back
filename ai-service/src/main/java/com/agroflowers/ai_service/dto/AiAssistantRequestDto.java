package com.agroflowers.ai_service.dto;

import jakarta.validation.constraints.NotBlank;

public record AiAssistantRequestDto(

        @NotBlank
        String question
) {
}

package com.agroflowers.ai_service.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ClaudeMessageRequestDto(
        String model,

        @JsonProperty("max_tokens")
        int maxTokens,

        String system,

        List<Message> messages
) {
    public record Message(String role, String content) {
    }
}

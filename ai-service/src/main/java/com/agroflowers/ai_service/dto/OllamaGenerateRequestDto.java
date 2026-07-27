package com.agroflowers.ai_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OllamaGenerateRequestDto(
        String model,
        String prompt,
        String system,
        boolean stream,
        Options options
) {
    public record Options(
            @JsonProperty("num_predict")
            int numPredict,

            @JsonProperty("num_ctx")
            int numCtx
    ) {
    }
}

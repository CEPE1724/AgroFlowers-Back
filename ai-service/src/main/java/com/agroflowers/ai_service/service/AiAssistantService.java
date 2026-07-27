package com.agroflowers.ai_service.service;

import com.agroflowers.ai_service.dto.AiAssistantResponseDto;

public interface AiAssistantService {

    AiAssistantResponseDto answer(String question, String bearerToken);
}

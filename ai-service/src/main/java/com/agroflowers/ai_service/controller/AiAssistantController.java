package com.agroflowers.ai_service.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agroflowers.ai_service.dto.AiAssistantRequestDto;
import com.agroflowers.ai_service.dto.AiAssistantResponseDto;
import com.agroflowers.ai_service.service.AiAssistantService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ai")
public class AiAssistantController {

    private final AiAssistantService aiAssistantService;

    public AiAssistantController(AiAssistantService aiAssistantService) {
        this.aiAssistantService = aiAssistantService;
    }

    @PostMapping("/assistant")
    public ResponseEntity<AiAssistantResponseDto> ask(
            @Valid @RequestBody AiAssistantRequestDto request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken
    ) {
        return ResponseEntity.ok(aiAssistantService.answer(request.question(), bearerToken));
    }
}

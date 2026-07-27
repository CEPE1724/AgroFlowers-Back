package com.agroflowers.auth_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agroflowers.auth_service.dto.CreateUserByAdminDto;
import com.agroflowers.auth_service.service.KeycloakAdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final KeycloakAdminService keycloakAdminService;

    public AdminUserController(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createUser(@Valid @RequestBody CreateUserByAdminDto request) {
        keycloakAdminService.createUserByAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

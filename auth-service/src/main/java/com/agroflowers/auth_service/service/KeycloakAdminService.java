package com.agroflowers.auth_service.service;

import com.agroflowers.auth_service.dto.CreateUserByAdminDto;
import com.agroflowers.auth_service.dto.LoginRequestDto;
import com.agroflowers.auth_service.dto.RegisterRequestDto;
import com.agroflowers.auth_service.dto.TokenResponseDto;

public interface KeycloakAdminService {

    void registerUser(RegisterRequestDto request);

    void createUserByAdmin(CreateUserByAdminDto request);

    TokenResponseDto login(LoginRequestDto request);
}

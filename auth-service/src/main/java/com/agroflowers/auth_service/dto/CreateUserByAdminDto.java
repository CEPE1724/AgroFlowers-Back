package com.agroflowers.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserByAdminDto(

        @NotBlank
        @Size(min = 4, max = 50)
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$")
        String username,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotBlank
        @Size(min = 8)
        String password,

        @NotBlank
        @Pattern(regexp = "^(SUPERVISOR|ADMIN)$")
        String role
) {
}

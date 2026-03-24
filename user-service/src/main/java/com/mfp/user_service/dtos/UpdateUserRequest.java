package com.mfp.user_service.dtos;

import jakarta.validation.constraints.Email;

public record UpdateUserRequest(
        String name,
        @Email(message = "Invalid email format")
        String email,
        Boolean active
) {
}

package com.mfp.user_service.dtos;

public record UserResponse(
        Long id,
        String name,
        String email,
        String role,
        Boolean active
) {
}

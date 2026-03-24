package com.mfp.user_service.dtos;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        String errorCode,
        int status,
        LocalDateTime timestamp
) {
}

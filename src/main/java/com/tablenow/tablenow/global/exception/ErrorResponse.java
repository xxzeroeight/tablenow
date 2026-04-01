package com.tablenow.tablenow.global.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse
(
        Instant timestamp,
        String code,
        String message,
        Map<String, Object> details,
        String exceptionType,
        int status
)
{
    public static ErrorResponse from(TablenowException e) {
        return new ErrorResponse(
                e.getTimestamp(),
                e.getErrorCode().name(),
                e.getErrorCode().getMessage(),
                e.getDetails(),
                e.getClass().getSimpleName(),
                e.getErrorCode().getStatus()
        );
    }
}

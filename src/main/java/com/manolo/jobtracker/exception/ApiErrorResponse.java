package com.manolo.jobtracker.exception;

import com.manolo.jobtracker.model.enums.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;

public record ApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        ErrorCode error,
        String message,
        String path,
        List<ApiValidationError> fieldErrors
) {}
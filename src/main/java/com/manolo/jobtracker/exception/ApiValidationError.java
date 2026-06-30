package com.manolo.jobtracker.exception;

public record ApiValidationError(
        String field,
        String message
) {}
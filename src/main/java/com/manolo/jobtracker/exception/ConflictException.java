package com.manolo.jobtracker.exception;

import com.manolo.jobtracker.model.enums.ErrorCode;

public class ConflictException extends RuntimeException {

    private final ErrorCode errorCode;

    public ConflictException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
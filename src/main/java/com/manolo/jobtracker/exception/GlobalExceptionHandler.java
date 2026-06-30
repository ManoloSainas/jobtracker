package com.manolo.jobtracker.exception;

import com.manolo.jobtracker.model.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // USER NOT FOUND
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(
            UserNotFoundException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiErrorResponse(
                        LocalDateTime.now(),
                        404,
                        ErrorCode.USER_NOT_FOUND,
                        ex.getMessage(),
                        request.getRequestURI(),
                        List.of()
                )
        );
    }

    // TAG NOT FOUND
    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleTagNotFound(
            TagNotFoundException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiErrorResponse(
                        LocalDateTime.now(),
                        404,
                        ErrorCode.TAG_NOT_FOUND,
                        ex.getMessage(),
                        request.getRequestURI(),
                        List.of()
                )
        );
    }

    // APPLICATION NOT FOUND
    @ExceptionHandler(ApplicationNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleApplicationNotFound(
            ApplicationNotFoundException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiErrorResponse(
                        LocalDateTime.now(),
                        404,
                        ErrorCode.APPLICATION_NOT_FOUND,
                        ex.getMessage(),
                        request.getRequestURI(),
                        List.of()
                )
        );
    }

    // BAD REQUEST
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.badRequest().body(
                new ApiErrorResponse(
                        LocalDateTime.now(),
                        400,
                        ex.getErrorCode(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        List.of()
                )
        );
    }

    // CONFLICT
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(
            ConflictException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ApiErrorResponse(
                        LocalDateTime.now(),
                        409,
                        ex.getErrorCode(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        List.of()
                )
        );
    }

    // VALIDATION
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        List<ApiValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new ApiValidationError(
                        err.getField(),
                        err.getDefaultMessage()
                ))
                .toList();

        return ResponseEntity.badRequest().body(
                new ApiErrorResponse(
                        LocalDateTime.now(),
                        400,
                        ErrorCode.VALIDATION_ERROR,
                        "Validation failed",
                        request.getRequestURI(),
                        errors
                )
        );
    }

    // GENERIC
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiErrorResponse(
                        LocalDateTime.now(),
                        500,
                        ErrorCode.INTERNAL_ERROR,
                        "Unexpected error occurred",
                        request.getRequestURI(),
                        List.of()
                )
        );
    }
}
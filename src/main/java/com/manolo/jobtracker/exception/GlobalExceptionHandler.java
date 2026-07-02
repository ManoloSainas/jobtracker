package com.manolo.jobtracker.exception;

import com.manolo.jobtracker.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // USER NOT FOUND
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(
            UserNotFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("UserNotFoundException: message={}, path={}",
                ex.getMessage(), request.getRequestURI());

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
        log.warn("TagNotFoundException: message={}, path={}",
                ex.getMessage(), request.getRequestURI());

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
        log.warn("ApplicationNotFoundException: message={}, path={}",
                ex.getMessage(), request.getRequestURI());

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
        log.warn("BadRequestException: message={}, path={}",
                ex.getMessage(), request.getRequestURI());

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
        log.warn("ConflictException: message={}, path={}",
                ex.getMessage(), request.getRequestURI());

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

        log.warn("Validation error on path={}", request.getRequestURI());

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

    // JSON NOT OK
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {

        log.warn("Malformed JSON request on path={}", request.getRequestURI());

        return ResponseEntity.badRequest().body(
                new ApiErrorResponse(
                        LocalDateTime.now(),
                        400,
                        ErrorCode.VALIDATION_ERROR,
                        "Richiesta non valida: controlla che tutti i campi abbiano il formato corretto",
                        request.getRequestURI(),
                        List.of()
                )
        );
    }

    // GENERIC (CRITICAL)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {

        log.error("Unexpected server error on path={}", request.getRequestURI(), ex);

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
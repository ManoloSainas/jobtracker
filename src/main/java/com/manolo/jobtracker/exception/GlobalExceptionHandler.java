package com.manolo.jobtracker.exception;

import com.manolo.jobtracker.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
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
    public ResponseEntity<ApiErrorResponseDTO> handleUserNotFound(
            UserNotFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("UserNotFoundException: message={}, path={}",
                ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiErrorResponseDTO(
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
    public ResponseEntity<ApiErrorResponseDTO> handleTagNotFound(
            TagNotFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("TagNotFoundException: message={}, path={}",
                ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiErrorResponseDTO(
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
    public ResponseEntity<ApiErrorResponseDTO> handleApplicationNotFound(
            ApplicationNotFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("ApplicationNotFoundException: message={}, path={}",
                ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiErrorResponseDTO(
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
    public ResponseEntity<ApiErrorResponseDTO> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        log.warn("BadRequestException: message={}, path={}",
                ex.getMessage(), request.getRequestURI());

        return ResponseEntity.badRequest().body(
                new ApiErrorResponseDTO(
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
    public ResponseEntity<ApiErrorResponseDTO> handleConflict(
            ConflictException ex,
            HttpServletRequest request
    ) {
        log.warn("ConflictException: message={}, path={}",
                ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ApiErrorResponseDTO(
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
    public ResponseEntity<ApiErrorResponseDTO> handleValidation(
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
                new ApiErrorResponseDTO(
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
    public ResponseEntity<ApiErrorResponseDTO> handleNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {

        log.warn("Malformed JSON request on path={}", request.getRequestURI());

        return ResponseEntity.badRequest().body(
                new ApiErrorResponseDTO(
                        LocalDateTime.now(),
                        400,
                        ErrorCode.VALIDATION_ERROR,
                        "Richiesta non valida: controlla che tutti i campi abbiano il formato corretto",
                        request.getRequestURI(),
                        List.of()
                )
        );
    }

    // INVALID CREDENTIALS
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request
    ) {

        log.warn("Authentication failed on path={}", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ApiErrorResponseDTO(
                        LocalDateTime.now(),
                        401,
                        ErrorCode.INVALID_CREDENTIALS,
                        "Credenziali non valide",
                        request.getRequestURI(),
                        List.of()
                )
        );
    }

    // GENERIC (CRITICAL)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDTO> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {

        log.error("Unexpected server error on path={}", request.getRequestURI(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiErrorResponseDTO(
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
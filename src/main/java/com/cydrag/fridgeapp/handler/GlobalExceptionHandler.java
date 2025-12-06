package com.cydrag.fridgeapp.handler;

import com.cydrag.fridgeapp.dto.response.ErrorResponse;
import com.cydrag.fridgeapp.exception.ResourceNotFoundException;
import com.cydrag.fridgeapp.exception.TokenRefreshException;
import com.cydrag.fridgeapp.exception.UserAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(Exception ex, HttpServletRequest request) {
        log.warn("Authentication failure from IP {}: {}", request.getRemoteAddr(), ex.getMessage());

        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "Authentication Failed",
                "Invalid email, password, or account status.",
                request
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        log.warn("Registration failed: {}", ex.getMessage());

        return buildResponse(HttpStatus.CONFLICT, "User Exists", ex.getMessage(), request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ErrorResponse> handleTokenRefresh(TokenRefreshException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Token Refresh Failed", ex.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "Data Conflict", "Operation failed due to invalid references or duplicates.", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        log.debug("Validation failed: {}", errorMessage);
        return buildResponse(HttpStatus.UNPROCESSABLE_CONTENT, "Validation Error", errorMessage, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobal(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred at {}", request.getRequestURI(), ex);

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error", "An unexpected error occurred", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccess(AccessDeniedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access Denied", ex.getMessage(), request);
    }

//    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
//        String message = String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
//                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());
//
//        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", message, request); // request is hard to get here sometimes
//    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String error, String message, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                status.value(),
                error,
                message,
                Instant.now(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, status);
    }
}

package com.maxxenergy.edap.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the EDAP application.
 * Provides consistent error responses across all controllers.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle general exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex, WebRequest request) {

        logger.error("Unhandled exception occurred: {}", ex.getMessage(), ex);

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Internal server error");
        response.put("message", "An unexpected error occurred. Please try again later.");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", getPath(request));

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle validation errors (IllegalArgumentException)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        logger.warn("Validation error: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Invalid input");
        response.put("message", ex.getMessage());
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", getPath(request));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle runtime exceptions
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {

        logger.error("Runtime exception occurred: {}", ex.getMessage(), ex);

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Runtime error");
        response.put("message", ex.getMessage());
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", getPath(request));

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle method argument validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        logger.warn("Method argument validation failed: {}", ex.getMessage());

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing // Keep first error if multiple for same field
                ));

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Validation failed");
        response.put("message", "Please check your input and try again");
        response.put("fieldErrors", fieldErrors);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", getPath(request));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle MongoDB related exceptions
     */
    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccessException(
            org.springframework.dao.DataAccessException ex, WebRequest request) {

        logger.error("Database access error: {}", ex.getMessage(), ex);

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Database error");
        response.put("message", "Unable to access data. Please try again later.");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", getPath(request));

        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Handle resource not found exceptions
     */
    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoHandlerFoundException(
            org.springframework.web.servlet.NoHandlerFoundException ex, WebRequest request) {

        logger.warn("Resource not found: {}", ex.getRequestURL());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Resource not found");
        response.put("message", "The requested resource could not be found");
        response.put("requestedUrl", ex.getRequestURL());
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", getPath(request));

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle security exceptions (if using Spring Security in future)
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
            org.springframework.security.access.AccessDeniedException ex, WebRequest request) {

        logger.warn("Access denied: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Access denied");
        response.put("message", "You do not have permission to access this resource");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", getPath(request));

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Handle authentication exceptions (if using Spring Security in future)
     */
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            org.springframework.security.core.AuthenticationException ex, WebRequest request) {

        logger.warn("Authentication failed: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Authentication required");
        response.put("message", "Please log in to access this resource");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", getPath(request));

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handle custom business logic exceptions
     */
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessLogicException(
            BusinessLogicException ex, WebRequest request) {

        logger.warn("Business logic error: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Business logic error");
        response.put("message", ex.getMessage());
        response.put("code", ex.getErrorCode());
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", getPath(request));

        return new ResponseEntity<>(response, ex.getHttpStatus());
    }

    /**
     * Extract request path from WebRequest
     */
    private String getPath(WebRequest request) {
        String path = request.getDescription(false);
        if (path != null && path.startsWith("uri=")) {
            return path.substring(4);
        }
        return path;
    }

    /**
     * Custom business logic exception class
     */
    public static class BusinessLogicException extends RuntimeException {
        private final String errorCode;
        private final HttpStatus httpStatus;

        public BusinessLogicException(String message, String errorCode, HttpStatus httpStatus) {
            super(message);
            this.errorCode = errorCode;
            this.httpStatus = httpStatus;
        }

        public BusinessLogicException(String message, String errorCode) {
            this(message, errorCode, HttpStatus.BAD_REQUEST);
        }

        public String getErrorCode() {
            return errorCode;
        }

        public HttpStatus getHttpStatus() {
            return httpStatus;
        }
    }
}
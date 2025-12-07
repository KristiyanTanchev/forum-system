package com.team3.forum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.team3.forum.controllers.rest")
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Not found", e.getMessage());
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthorizationException(AuthorizationException e) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", e.getMessage());
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateEntityException(DuplicateEntityException e) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Conflict", e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(BadCredentialsException e) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid username or password");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid username or password");
    }

    @ExceptionHandler(FolderNotEmptyException.class)
    public ResponseEntity<Map<String, Object>> handleFolderNotEmptyException(FolderNotEmptyException e) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Conflict", e.getMessage());
    }


    @ExceptionHandler(EntityUpdateConflictException.class)
    public ResponseEntity<Map<String, Object>> handleEntityUpdateConflictException(EntityUpdateConflictException e) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Conflict", e.getMessage());
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Map<String, Object>> handleLockedException(LockedException e) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Forbidden", e.getMessage());
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<Map<String, Object>> handleFileStorageException(FileStorageException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", e.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String error, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        return ResponseEntity.status(status).body(errorResponse);

    }


}

package com.okanetransfer.shared.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 400 Bad Request ───────────────────────────────────────────────────────

    // erreurs de validation @Valid sur les DTOs request
    // ex : @NotBlank, @Email, @Pattern non respectés
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // erreurs de validation sur les paramètres de méthode (@RequestParam, @PathVariable)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(400, ex.getMessage()));
    }

    @ExceptionHandler(PlafondDepasseException.class)
    public ResponseEntity<ErrorResponse> handlePlafondDepasse(
            PlafondDepasseException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(400, ex.getMessage()));
    }

    @ExceptionHandler(CodeRetraitInvalideException.class)
    public ResponseEntity<ErrorResponse> handleCodeRetraitInvalide(
            CodeRetraitInvalideException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(400, ex.getMessage()));
    }

    @ExceptionHandler(CorridorInactifException.class)
    public ResponseEntity<ErrorResponse> handleCorridorInactif(
            CorridorInactifException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(400, ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(400, ex.getMessage()));
    }

    // ── 403 Forbidden ─────────────────────────────────────────────────────────

    @ExceptionHandler(AccesRefuseException.class)
    public ResponseEntity<ErrorResponse> handleAccesRefuse(
            AccesRefuseException ex) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse(403, ex.getMessage()));
    }

    @ExceptionHandler(OFACViolationException.class)
    public ResponseEntity<ErrorResponse> handleOFACViolation(
            OFACViolationException ex) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse(403, ex.getMessage()));
    }

    // Spring Security lance AccessDeniedException pour les @PreAuthorize
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleSpringAccessDenied(
            AccessDeniedException ex) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse(403, "Accès refusé : droits insuffisants"));
    }

    // ── 404 Not Found ─────────────────────────────────────────────────────────

    @ExceptionHandler(TransfertNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            TransfertNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(404, ex.getMessage()));
    }

    // ── 500 Internal Server Error ─────────────────────────────────────────────

    // capture toutes les exceptions non gérées
    // évite d'exposer des stack traces en production
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(500, "Erreur interne du serveur"));
    }
}

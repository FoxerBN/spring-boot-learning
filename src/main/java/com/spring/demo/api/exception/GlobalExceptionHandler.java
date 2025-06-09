package com.spring.demo.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
    return buildResponse(ex, HttpStatus.NOT_FOUND, req.getRequestURI());
  }

  @ExceptionHandler(InternalErrorException.class)
  public ResponseEntity<Map<String, Object>> handleInternalError(InternalErrorException ex, HttpServletRequest req) {
    return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, req.getRequestURI());
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex, HttpServletRequest req) {
    return buildResponse(ex, HttpStatus.BAD_REQUEST, req.getRequestURI());
  }

  private ResponseEntity<Map<String, Object>> buildResponse(Exception ex, HttpStatus status, String path) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", Instant.now());
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());
    body.put("message", ex.getMessage());
    body.put("path", path);
    return ResponseEntity.status(status).body(body);
  }
}

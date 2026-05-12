package com.example.murinofm.exception;

import com.example.murinofm.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AppException.class)
  public ResponseEntity<ErrorResponse> handleAppEx(AppException ex, HttpServletRequest request) {
    log.error("AppException: {}", ex.getMessage());
    return buildResponse(HttpStatus.NOT_FOUND, "AppException", ex.getMessage(), request);
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex, HttpServletRequest request) {
    log.warn("Conflict: {}", ex.getMessage());
    return buildResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                        HttpServletRequest request) {
    List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors()
        .stream()
        .map(fe -> ErrorResponse.FieldError.builder()
            .field(fe.getField())
            .message(fe.getDefaultMessage())
            .build())
        .collect(Collectors.toList());

    log.warn("Ошибка валидации: {} полей - {}", fieldErrors.size(),
        fieldErrors.stream().map(ErrorResponse.FieldError::getField).collect(Collectors.joining(", ")));

    ErrorResponse response = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Validation Failure")
        .message("Ошибка валидации входных данных")
        .path(request.getRequestURI())
        .fieldErrors(fieldErrors)
        .build();
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex, HttpServletRequest request) {
    log.error("Непредвиденная ошибка: {}", ex.getMessage(), ex);
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
        "Произошла внутренняя ошибка сервера", request);
  }

  private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String error,
                                                      String message, HttpServletRequest request) {
    ErrorResponse response = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(status.value())
        .error(error)
        .message(message)
        .path(request.getRequestURI())
        .build();
    return new ResponseEntity<>(response, status);
  }
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
                                                             HttpServletRequest request) {
    log.warn("Некорректные данные: {}", ex.getMessage());
    return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
  }
  @ExceptionHandler(BulkOperationException.class)
  public ResponseEntity<ErrorResponse> handleBulk(BulkOperationException ex, HttpServletRequest request) {
    List<ErrorResponse.FieldError> fieldErrors = ex.getErrors().stream()
        .map(e -> ErrorResponse.FieldError.builder()
            .field("tracks[" + e.index() + "]")
            .message(e.reason())
            .build())
        .collect(Collectors.toList());

    log.warn("Bulk operation validation failed: {}", fieldErrors);
    ErrorResponse response = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Bulk Validation Failure")
        .message(ex.getMessage())
        .path(request.getRequestURI())
        .fieldErrors(fieldErrors)
        .build();
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }
}
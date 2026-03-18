package com.example.murinofm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Object> handleRuntime(RuntimeException ex) {
    return new ResponseEntity<>(
        Map.of(
            "timestamp", LocalDateTime.now(),
            "message", ex.getMessage(),
            "status", HttpStatus.NOT_FOUND.value()
        ),
        HttpStatus.NOT_FOUND
    );
  }
}
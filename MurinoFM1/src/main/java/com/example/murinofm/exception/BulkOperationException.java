package com.example.murinofm.exception;

import lombok.Getter;
import java.util.List;

@Getter
public class BulkOperationException extends RuntimeException {
  private final List<BulkError> errors;

  public BulkOperationException(String message, List<BulkError> errors) {
    super(message);
    this.errors = errors;
  }

  public record BulkError(int index, String trackTitle, String reason) {}
}
package com.nantaaditya.dbmigration.model.exception;

import java.util.Collections;
import java.util.Map;
import lombok.Data;

@Data
public class InvalidParameterException extends RuntimeException {
  private String message;
  private Map<String, String> errors;

  public InvalidParameterException(String message) {
    super(message);
    this.errors = Collections.emptyMap();
  }

  public InvalidParameterException(String message, Map<String, String> errors) {
    super(message);
    this.errors = errors;
  }
}

package com.nantaaditya.dbmigration.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

  @ResponseBody
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> illegalArgument(IllegalArgumentException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ResponseBody
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, String>> constraintValidationException(ConstraintViolationException e) {
    Map<String, String> errors = new HashMap<>();
    for (ConstraintViolation violation : e.getConstraintViolations()) {
      errors.put(violation.getPropertyPath().toString(), violation.getMessage());
    }
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  @ResponseBody
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> methodArgumentNotValidException(MethodArgumentNotValidException e) {
    Map<String, String> errors = new HashMap<>();
    for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
      errors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }
    for (ObjectError objectError : e.getBindingResult().getAllErrors()) {
      errors.put(getObjectErrorField(objectError.getObjectName()), objectError.getDefaultMessage());
    }
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  private String getObjectErrorField(String objectName) {
    switch (objectName) {
      case "getDatabaseCredentialRequestDTO" -> {
        return "passphrase";
      }
      default -> {
        return objectName;
      }
    }
  }
}

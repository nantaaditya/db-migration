package com.nantaaditya.dbmigration.validator;

import com.nantaaditya.dbmigration.repository.DatabaseCredentialRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DatabaseIdMustExistsValidator implements ConstraintValidator<DatabaseIdMustExists, String> {

  @Autowired
  private DatabaseCredentialRepository databaseCredentialRepository;

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (!StringUtils.hasLength(value)) return false;
    return databaseCredentialRepository.existsById(value);
  }
}

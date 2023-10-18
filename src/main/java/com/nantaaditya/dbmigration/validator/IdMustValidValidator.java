package com.nantaaditya.dbmigration.validator;

import com.nantaaditya.dbmigration.entity.MigrationVersion;
import com.nantaaditya.dbmigration.repository.MigrationVersionRepository;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdMustValidValidator implements ConstraintValidator<IdMustValid, Long> {

  @Autowired
  private MigrationVersionRepository migrationVersionRepository;

  @Override
  public boolean isValid(Long value, ConstraintValidatorContext context) {
    MigrationVersion migrationVersion = migrationVersionRepository.findFirstByOrderByIdDesc();
    if (migrationVersion == null) return true;
    return value > migrationVersion.getId();
  }
}

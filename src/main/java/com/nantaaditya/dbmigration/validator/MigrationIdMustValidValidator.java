package com.nantaaditya.dbmigration.validator;

import com.nantaaditya.dbmigration.entity.MigrationVersion;
import com.nantaaditya.dbmigration.model.request.ICreateMigrationRequestDTO;
import com.nantaaditya.dbmigration.repository.MigrationVersionRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MigrationIdMustValidValidator implements ConstraintValidator<MigrationIdMustValid, ICreateMigrationRequestDTO> {

  @Autowired
  private MigrationVersionRepository migrationVersionRepository;

  @Override
  public boolean isValid(ICreateMigrationRequestDTO value, ConstraintValidatorContext context) {
    MigrationVersion migrationVersion = migrationVersionRepository.findFirstByDatabaseIdOrderByIdDesc(
        value.getDatabaseId());
    if (migrationVersion == null) return true;
    return value.getIds()
        .stream()
        .allMatch(version -> version > migrationVersion.getId());
  }
}

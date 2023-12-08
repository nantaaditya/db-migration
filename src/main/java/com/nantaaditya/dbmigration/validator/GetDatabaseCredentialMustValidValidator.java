package com.nantaaditya.dbmigration.validator;

import com.nantaaditya.dbmigration.model.request.IGetDatabaseCredentialRequestDTO;
import com.nantaaditya.dbmigration.repository.DatabaseCredentialRepository;
import com.nantaaditya.dbmigration.util.RSAUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetDatabaseCredentialMustValidValidator implements ConstraintValidator<GetDatabaseCredentialMustValid, IGetDatabaseCredentialRequestDTO> {

  @Autowired
  private DatabaseCredentialRepository databaseCredentialRepository;

  @Autowired
  private RSAUtil rsaUtil;

  @Override
  public boolean isValid(IGetDatabaseCredentialRequestDTO value, ConstraintValidatorContext context) {
    return databaseCredentialRepository.findById(value.getDatabaseId())
        .stream()
        .anyMatch(databaseCredential -> {
          String passphrase = value.getPassphrase();
          String decryptPassphrase = rsaUtil.decrypt(databaseCredential.getPassphrase());
          return StringUtils.equals(passphrase, decryptPassphrase);
        });
  }
}

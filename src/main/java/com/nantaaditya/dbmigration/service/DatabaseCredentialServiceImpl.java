package com.nantaaditya.dbmigration.service;

import com.nantaaditya.dbmigration.entity.DatabaseCredential;
import com.nantaaditya.dbmigration.model.exception.InvalidParameterException;
import com.nantaaditya.dbmigration.model.request.BaseDatabaseCredentialRequestDTO;
import com.nantaaditya.dbmigration.model.request.GetDatabaseCredentialRequestDTO;
import com.nantaaditya.dbmigration.model.request.UpdateDatabaseCredentialRequestDTO;
import com.nantaaditya.dbmigration.model.response.BaseDatabaseCredentialDTO;
import com.nantaaditya.dbmigration.model.response.GetDatabaseCredentialResponseDTO;
import com.nantaaditya.dbmigration.repository.DatabaseCredentialRepository;
import com.nantaaditya.dbmigration.util.RSAUtil;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseCredentialServiceImpl implements DatabaseCredentialService {

  private final DatabaseCredentialRepository databaseCredentialRepository;

  private final RSAUtil rsaUtil;

  @Override
  public BaseDatabaseCredentialDTO create(
      BaseDatabaseCredentialRequestDTO request) {
    DatabaseCredential databaseCredential = databaseCredentialRepository.save(DatabaseCredential.create(request, rsaUtil));
    BaseDatabaseCredentialDTO response = new BaseDatabaseCredentialDTO();
    BeanUtils.copyProperties(databaseCredential, response);
    return response;
  }

  @Override
  public BaseDatabaseCredentialDTO update(UpdateDatabaseCredentialRequestDTO request) {
    Optional<DatabaseCredential> maybeDbCredential = databaseCredentialRepository.findById(request.getId());
    if (maybeDbCredential.isEmpty()) {
      throw new InvalidParameterException("database credential not exists", Map.of("id", "NotExists"));
    }
    DatabaseCredential databaseCredential = databaseCredentialRepository.save(DatabaseCredential
        .update(request, maybeDbCredential.get(), rsaUtil));
    BaseDatabaseCredentialDTO response = new BaseDatabaseCredentialDTO();
    BeanUtils.copyProperties(databaseCredential, response);
    return response;
  }

  @Override
  public List<BaseDatabaseCredentialDTO> findAll() {
    return databaseCredentialRepository.findAll()
        .stream()
        .map(databaseCredential -> {
          BaseDatabaseCredentialDTO response = new BaseDatabaseCredentialDTO();
          BeanUtils.copyProperties(databaseCredential, response);
          return response;
        }).toList();
  }

  @Override
  public GetDatabaseCredentialResponseDTO find(GetDatabaseCredentialRequestDTO request) {
    return databaseCredentialRepository.findById(request.getDatabaseId())
        .map(databaseCredential -> {
          GetDatabaseCredentialResponseDTO response = new GetDatabaseCredentialResponseDTO();
          BeanUtils.copyProperties(databaseCredential, response);
          response.setDbUser(rsaUtil.decrypt(response.getDbUser()));
          response.setDbPassword(rsaUtil.decryptPassword(response.getDbPassword()));
          response.setPassphrase(rsaUtil.decrypt(response.getPassphrase()));
          return response;
        }).orElseThrow(() -> new InvalidParameterException("database credential not exists", Map.of("databaseId", "NotExists")));
  }
}

package com.nantaaditya.dbmigration.service;

import com.nantaaditya.dbmigration.model.request.BaseDatabaseCredentialRequestDTO;
import com.nantaaditya.dbmigration.model.request.GetDatabaseCredentialRequestDTO;
import com.nantaaditya.dbmigration.model.request.UpdateDatabaseCredentialRequestDTO;
import com.nantaaditya.dbmigration.model.response.BaseDatabaseCredentialDTO;
import com.nantaaditya.dbmigration.model.response.GetDatabaseCredentialResponseDTO;
import java.util.List;

public interface DatabaseCredentialService {
  BaseDatabaseCredentialDTO create(
      BaseDatabaseCredentialRequestDTO request);

  BaseDatabaseCredentialDTO update(
      UpdateDatabaseCredentialRequestDTO request);

  List<BaseDatabaseCredentialDTO> findAll();

  GetDatabaseCredentialResponseDTO find(GetDatabaseCredentialRequestDTO request);
}

package com.nantaaditya.dbmigration.service;

import com.nantaaditya.dbmigration.model.MigrationRequestDTO;
import com.nantaaditya.dbmigration.model.MigrationResponseDTO;
import com.nantaaditya.dbmigration.model.SchemaResponseDTO;
import java.util.Set;

public interface MigrationService {

  Set<MigrationResponseDTO> runMigration(Set<MigrationRequestDTO> requests);

  Set<MigrationResponseDTO> runRollback(long version);

  SchemaResponseDTO getSchema();
}

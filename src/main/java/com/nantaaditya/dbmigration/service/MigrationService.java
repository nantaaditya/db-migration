package com.nantaaditya.dbmigration.service;

import com.nantaaditya.dbmigration.model.request.BaseMigrationRequestDTO;
import com.nantaaditya.dbmigration.model.request.CreateMigrationRequestDTO;
import com.nantaaditya.dbmigration.model.request.RollbackMigrationRequestDTO;
import com.nantaaditya.dbmigration.model.response.MigrationResponseDTO;
import com.nantaaditya.dbmigration.model.response.SchemaResponseDTO;
import com.nantaaditya.dbmigration.model.response.SequenceResponseDTO;
import jakarta.validation.Valid;
import java.util.Set;
import org.springframework.validation.annotation.Validated;

@Validated
public interface MigrationService {

  Set<MigrationResponseDTO> runMigration(CreateMigrationRequestDTO request);

  Set<MigrationResponseDTO> runRollback(RollbackMigrationRequestDTO request);

  SchemaResponseDTO getSchema(@Valid BaseMigrationRequestDTO request);

  SequenceResponseDTO getLastSequence(@Valid BaseMigrationRequestDTO request);
}

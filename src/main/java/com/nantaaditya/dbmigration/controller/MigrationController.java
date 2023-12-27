package com.nantaaditya.dbmigration.controller;

import com.nantaaditya.dbmigration.model.request.BaseMigrationRequestDTO;
import com.nantaaditya.dbmigration.model.request.CreateMigrationRequestDTO;
import com.nantaaditya.dbmigration.model.request.RollbackMigrationRequestDTO;
import com.nantaaditya.dbmigration.model.response.MigrationResponseDTO;
import com.nantaaditya.dbmigration.model.response.SchemaResponseDTO;
import com.nantaaditya.dbmigration.model.response.SequenceResponseDTO;
import com.nantaaditya.dbmigration.service.MigrationService;
import jakarta.validation.Valid;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Validated
@RequestMapping(value = "/api/migration")
public class MigrationController {

  @Autowired
  private MigrationService migrationService;

  @PostMapping(value = "/_migrate",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Set<MigrationResponseDTO>> migrate(@RequestBody @Valid
    CreateMigrationRequestDTO request) {
    return new ResponseEntity<>(migrationService.runMigration(request), HttpStatus.OK);
  }

  @PostMapping(value = "/{databaseId}/_migrate",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Set<MigrationResponseDTO>> upload(@PathVariable String databaseId,
      @RequestPart MultipartFile migrationFile, @RequestPart MultipartFile rollbackFile) {
    return new ResponseEntity<>(migrationService.uploadMigration(databaseId, migrationFile, rollbackFile), HttpStatus.OK);
  }

  @DeleteMapping(value = "/_rollback",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Set<MigrationResponseDTO>> rollback(@RequestBody @Valid
    RollbackMigrationRequestDTO request) {
    return new ResponseEntity<>(migrationService.runRollback(request), HttpStatus.OK);
  }

  @GetMapping(value = "/schema/{databaseId}",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<SchemaResponseDTO> schema(@PathVariable String databaseId) {
    BaseMigrationRequestDTO request = new BaseMigrationRequestDTO();
    request.setDatabaseId(databaseId);
    return new ResponseEntity<>(migrationService.getSchema(request), HttpStatus.OK);
  }

  @GetMapping(value = "/last-sequence/{databaseId}",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<SequenceResponseDTO> lastSequence(@PathVariable String databaseId) {
    BaseMigrationRequestDTO request = new BaseMigrationRequestDTO();
    request.setDatabaseId(databaseId);
    return new ResponseEntity<>(migrationService.getLastSequence(request), HttpStatus.OK);
  }
}

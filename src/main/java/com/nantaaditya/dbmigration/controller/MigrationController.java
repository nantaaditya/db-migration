package com.nantaaditya.dbmigration.controller;

import com.nantaaditya.dbmigration.model.MigrationRequestDTO;
import com.nantaaditya.dbmigration.model.MigrationResponseDTO;
import com.nantaaditya.dbmigration.model.SchemaResponseDTO;
import com.nantaaditya.dbmigration.service.MigrationService;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.Min;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class MigrationController {

  @Autowired
  private MigrationService migrationService;

  @PostMapping(value = "/api/migrate",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Set<MigrationResponseDTO>> migrate(@RequestBody @Valid Set<MigrationRequestDTO> requests) {
    return new ResponseEntity<>(migrationService.runMigration(requests), HttpStatus.OK);
  }

  @DeleteMapping(value = "/api/rollback/{version}",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Set<MigrationResponseDTO>> rollback(@PathVariable @Min(value = 0, message = "must positive") long version) {
    return new ResponseEntity<>(migrationService.runRollback(version), HttpStatus.OK);
  }

  @GetMapping(value = "/api/schema",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<SchemaResponseDTO> schema() {
    return new ResponseEntity<>(migrationService.getSchema(), HttpStatus.OK);
  }
}

package com.nantaaditya.dbmigration.controller;

import com.nantaaditya.dbmigration.model.request.BaseDatabaseCredentialRequestDTO;
import com.nantaaditya.dbmigration.model.request.GetDatabaseCredentialRequestDTO;
import com.nantaaditya.dbmigration.model.request.UpdateDatabaseCredentialRequestDTO;
import com.nantaaditya.dbmigration.model.response.BaseDatabaseCredentialDTO;
import com.nantaaditya.dbmigration.model.response.GetDatabaseCredentialResponseDTO;
import com.nantaaditya.dbmigration.service.DatabaseCredentialService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping(value = "/api/database-credential")
public class DatabaseCredentialController {

  @Autowired
  private DatabaseCredentialService databaseCredentialService;

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<BaseDatabaseCredentialDTO> create(@RequestBody @Valid
    BaseDatabaseCredentialRequestDTO request) {
    return new ResponseEntity<>(databaseCredentialService.create(request), HttpStatus.OK);
  }

  @PutMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<BaseDatabaseCredentialDTO> update(@RequestBody @Valid
    UpdateDatabaseCredentialRequestDTO request) {
    return new ResponseEntity<>(databaseCredentialService.update(request), HttpStatus.OK);
  }

  @GetMapping(
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<List<BaseDatabaseCredentialDTO>> findAll() {
    return new ResponseEntity<>(databaseCredentialService.findAll(), HttpStatus.OK);
  }

  @PostMapping(value = "/_get",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<GetDatabaseCredentialResponseDTO> find(@RequestBody @Valid
      GetDatabaseCredentialRequestDTO request) {
    return new ResponseEntity<>(databaseCredentialService.find(request), HttpStatus.OK);
  }
}

package com.nantaaditya.dbmigration.model;

import com.nantaaditya.dbmigration.entity.MigrationVersion;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SchemaResponseDTO {

  private Set<String> migrations;

  private Set<String> rollbacks;

  public static SchemaResponseDTO from(List<MigrationVersion> mvs) {
    SchemaResponseDTO dto = new SchemaResponseDTO();
    dto.setMigrations(mvs.stream().map(MigrationVersion::getMigration).collect(Collectors.toSet()));
    dto.setRollbacks(mvs.stream().map(MigrationVersion::getRollback).collect(Collectors.toSet()));
    return dto;
  }
}

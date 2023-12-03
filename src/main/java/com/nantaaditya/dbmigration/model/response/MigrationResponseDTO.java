package com.nantaaditya.dbmigration.model.response;

import com.nantaaditya.dbmigration.entity.MigrationVersion;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MigrationResponseDTO {

  private long id;
  private String databaseId;
  private String migration;
  private String rollback;
  private int status;

  public static Set<MigrationResponseDTO> from(MigrationVersion[] mvs) {
    return Stream.of(mvs)
        .map(MigrationResponseDTO::from)
        .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingLong(MigrationResponseDTO::getId))));
  }

  public static MigrationResponseDTO from(MigrationVersion mv) {
    MigrationResponseDTO dto = new MigrationResponseDTO();
    dto.setId(mv.getId());
    dto.setDatabaseId(mv.getDatabaseId());
    dto.setMigration(mv.getMigration());
    dto.setRollback(mv.getRollback());
    dto.setStatus(mv.getMigrationStatus());
    return dto;
  }
}

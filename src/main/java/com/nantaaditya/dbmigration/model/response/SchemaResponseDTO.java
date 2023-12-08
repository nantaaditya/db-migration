package com.nantaaditya.dbmigration.model.response;

import com.nantaaditya.dbmigration.entity.MigrationVersion;
import java.util.Comparator;
import java.util.LinkedHashSet;
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
    dto.setMigrations(mvs.stream()
        .sorted(Comparator.comparing(MigrationVersion::getId))
        .map(MigrationVersion::getMigration)
        .collect(Collectors.toCollection(LinkedHashSet::new))
    );
    dto.setRollbacks(mvs.stream()
        .sorted(Comparator.comparing(MigrationVersion::getId))
        .map(MigrationVersion::getRollback)
        .collect(Collectors.toCollection(LinkedHashSet::new))
    );
    return dto;
  }
}

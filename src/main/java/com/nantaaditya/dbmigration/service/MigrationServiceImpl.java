package com.nantaaditya.dbmigration.service;

import com.nantaaditya.dbmigration.entity.MigrationHistory;
import com.nantaaditya.dbmigration.entity.MigrationVersion;
import com.nantaaditya.dbmigration.model.MigrationRequestDTO;
import com.nantaaditya.dbmigration.model.MigrationResponseDTO;
import com.nantaaditya.dbmigration.model.SchemaResponseDTO;
import com.nantaaditya.dbmigration.model.SequenceResponseDTO;
import com.nantaaditya.dbmigration.repository.MigrationHistoryRepository;
import com.nantaaditya.dbmigration.repository.MigrationVersionRepository;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MigrationServiceImpl implements MigrationService {

  @Autowired
  private MigrationVersionRepository migrationVersionRepository;

  @Autowired
  private MigrationHistoryRepository migrationHistoryRepository;

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Value("${spring.datasource.username}")
  private String username;

  @Value("${spring.datasource.password}")
  private String password;

  @Override
  public Set<MigrationResponseDTO> runMigration(Set<MigrationRequestDTO> requests) {
    if (requests.isEmpty()) return new HashSet<>();

    MigrationVersion[] migrationVersions = migrationVersionRepository.saveAll(MigrationVersion.from(requests))
        .stream()
        .sorted(Comparator.comparingLong(MigrationVersion::getId))
        .toArray(MigrationVersion[]::new);

    Connection connection = null;
    Statement statement = null;
    int result = 0;

    for (int i=0; i < migrationVersions.length; i++) {
      MigrationVersion mv = migrationVersions[i];
      try {
        connection = DriverManager.getConnection(dbUrl, username, password);
        statement = connection.createStatement();
        result = statement.executeUpdate(mv.getMigration());
        log.info("#MIGRATION - query [{}] result [{}]", mv.getMigration(), result);

        if (mv.isValidToMigrate()) {
          MigrationVersion.afterMigrate(mv, MigrationVersion.SUCCESS_MIGRATION);
        } else {
          MigrationVersion.afterMigrate(mv, MigrationVersion.FAILED_MIGRATION);
        }
        migrationVersionRepository.save(mv);
        migrationHistoryRepository.save(MigrationHistory.from(mv.getMigration(), mv.getMigrationStatus()));
      } catch (Throwable e) {
        log.error("#MIGRATION - query [{}] failed to execute, ", mv.getMigration(), e);
        MigrationVersion.afterMigrate(mv, MigrationVersion.FAILED_MIGRATION);
        migrationVersionRepository.save(mv);
        migrationHistoryRepository.save(MigrationHistory.from(mv.getMigration(), mv.getMigrationStatus()));
      } finally {
        try {
          if (statement != null) statement.close();
          if (connection != null) connection.close();
        } catch (Exception ex) {
          log.error("#MIGRATION - query [{}] failed to close, ", mv.getMigration(), ex);
        }
      }
    }

    return MigrationResponseDTO.from(migrationVersions);
  }

  @Override
  public Set<MigrationResponseDTO> runRollback(long version) {
    List<MigrationVersion> migrationVersions = migrationVersionRepository.findByIdAfterAndMigrationStatusAndRollbackDateIsNullOrderByIdDesc(version, MigrationVersion.SUCCESS_MIGRATION);

    if (migrationVersions.isEmpty()) return new HashSet<>();

    Connection connection = null;
    Statement statement = null;
    int result = 0;

    MigrationVersion[] mvs = migrationVersions
        .stream()
        .sorted(Comparator.comparingLong(MigrationVersion::getId))
        .toArray(MigrationVersion[]::new);


    for (int i=0; i< mvs.length; i++) {
      MigrationVersion mv = mvs[i];
      try {
        connection = DriverManager.getConnection(dbUrl, username, password);
        statement = connection.createStatement();
        result = statement.executeUpdate(mv.getRollback());

        log.info("#MIGRATION - rollback query [{}] result [{}]", mv.getRollback(), result);
        MigrationVersion.afterRollback(mv, MigrationVersion.SUCCESS_ROLLBACK_MIGRATION);
        migrationVersionRepository.save(mv);
        migrationHistoryRepository.save(MigrationHistory.from(mv.getRollback(), mv.getMigrationStatus()));
      } catch (Exception e) {
        log.error("#MIGRATION - rollback query [{}] failed to execute, ", mv.getRollback(), e);
        MigrationVersion.afterRollback(mv, MigrationVersion.FAILED_MIGRATION);
        migrationVersionRepository.save(mv);
        migrationHistoryRepository.save(MigrationHistory.from(mv.getRollback(), mv.getMigrationStatus()));
      } finally {
        try {
          if (statement != null) statement.close();
          if (connection != null) connection.close();
        } catch (Exception ex) {
          log.error("#MIGRATION - rollback query [{}] failed to close, ", mv.getMigration(), ex);
        }
      }
    }

    return MigrationResponseDTO.from(mvs);
  }

  @Override
  public SchemaResponseDTO getSchema() {
    List<MigrationVersion> migrationVersions = migrationVersionRepository.findAll();

    migrationVersions.sort(Comparator.comparing(MigrationVersion::getId));
    return SchemaResponseDTO.from(migrationVersions);
  }

  @Override
  public SequenceResponseDTO getLastSequence() {
    MigrationVersion migrationVersion = migrationVersionRepository.findFirstByOrderByIdDesc();
    SequenceResponseDTO response = new SequenceResponseDTO();

    if (migrationVersion == null) {
      response.setLastSequence(0);
      return response;
    }

    response.setLastSequence(migrationVersion.getId());
    return response;
  }
}

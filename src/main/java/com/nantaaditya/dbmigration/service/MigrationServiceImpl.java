package com.nantaaditya.dbmigration.service;

import com.nantaaditya.dbmigration.entity.DatabaseCredential;
import com.nantaaditya.dbmigration.entity.MigrationHistory;
import com.nantaaditya.dbmigration.entity.MigrationVersion;
import com.nantaaditya.dbmigration.model.request.BaseMigrationRequestDTO;
import com.nantaaditya.dbmigration.model.request.CreateMigrationRequestDTO;
import com.nantaaditya.dbmigration.model.request.RollbackMigrationRequestDTO;
import com.nantaaditya.dbmigration.model.response.MigrationResponseDTO;
import com.nantaaditya.dbmigration.model.response.SchemaResponseDTO;
import com.nantaaditya.dbmigration.model.response.SequenceResponseDTO;
import com.nantaaditya.dbmigration.repository.DatabaseCredentialRepository;
import com.nantaaditya.dbmigration.repository.MigrationHistoryRepository;
import com.nantaaditya.dbmigration.repository.MigrationVersionRepository;
import com.nantaaditya.dbmigration.util.RSAUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MigrationServiceImpl implements MigrationService {

  @Autowired
  private MigrationVersionRepository migrationVersionRepository;

  @Autowired
  private MigrationHistoryRepository migrationHistoryRepository;

  @Autowired
  private DatabaseCredentialRepository databaseCredentialRepository;

  @Autowired
  private RSAUtil rsaUtil;

  private static final String JDBC_FORMAT = "jdbc:postgresql://%s/%s";

  @Override
  public Set<MigrationResponseDTO> runMigration(CreateMigrationRequestDTO request) {
    MigrationVersion[] migrationVersions = migrationVersionRepository.saveAll(
          MigrationVersion.from(request.getDatabaseId(), request.getMigrations())
        )
        .stream()
        .sorted(Comparator.comparingLong(MigrationVersion::getId))
        .toArray(MigrationVersion[]::new);

    Credential credential = getDatabaseCredential(request.getDatabaseId());

    Connection connection = null;
    Statement statement = null;
    int result = 0;


    for (int i=0; i < migrationVersions.length; i++) {
      MigrationVersion mv = migrationVersions[i];
      try {
        connection = DriverManager.getConnection(credential.host(), credential.user(), credential.password());
        statement = connection.createStatement();
        result = statement.executeUpdate(mv.getMigration());
        log.info("#MIGRATION - [{}] query [{}] result [{}]", credential.name(), mv.getMigration(), result);

        if (mv.isValidToMigrate()) {
          MigrationVersion.afterMigrate(mv, MigrationVersion.SUCCESS_MIGRATION);
        } else {
          MigrationVersion.afterMigrate(mv, MigrationVersion.FAILED_MIGRATION);
        }
        migrationVersionRepository.save(mv);
        migrationHistoryRepository.save(MigrationHistory.from(mv.getDatabaseId(), mv.getMigration(), mv.getMigrationStatus()));
      } catch (Throwable e) {
        log.error("#MIGRATION - [{}] query [{}] failed to execute, ", credential.name(), mv.getMigration(), e);
        MigrationVersion.afterMigrate(mv, MigrationVersion.FAILED_MIGRATION);
        migrationVersionRepository.save(mv);
        migrationHistoryRepository.save(MigrationHistory.from(credential.name(), mv.getMigration(), mv.getMigrationStatus()));
      } finally {
        try {
          if (statement != null) statement.close();
          if (connection != null) connection.close();
        } catch (Exception ex) {
          log.error("#MIGRATION - [{}] query [{}] failed to close, ", credential.name(), mv.getMigration(), ex);
        }
      }
    }

    return MigrationResponseDTO.from(migrationVersions);
  }

  @Override
  public Set<MigrationResponseDTO> runRollback(RollbackMigrationRequestDTO request) {
    List<MigrationVersion> migrationVersions = migrationVersionRepository
        .findByDatabaseIdAndIdAfterAndMigrationStatusAndRollbackDateIsNullOrderByIdDesc(
            request.getDatabaseId(), request.getVersion(), MigrationVersion.SUCCESS_MIGRATION);

    if (migrationVersions.isEmpty()) return new HashSet<>();

    Credential credential = getDatabaseCredential(request.getDatabaseId());

    Connection connection = null;
    Statement statement = null;
    int result = 0;

    MigrationVersion[] mvs = migrationVersions
        .stream()
        .sorted(Comparator.comparingLong(MigrationVersion::getId).reversed())
        .toArray(MigrationVersion[]::new);


    for (int i=0; i< mvs.length; i++) {
      MigrationVersion mv = mvs[i];
      try {
        connection = DriverManager.getConnection(credential.host(), credential.user(), credential.password());
        statement = connection.createStatement();
        result = statement.executeUpdate(mv.getRollback());

        log.info("#MIGRATION - [{}] rollback query [{}] result [{}]", credential.name(), mv.getRollback(), result);
        MigrationVersion.afterRollback(mv, MigrationVersion.SUCCESS_ROLLBACK_MIGRATION);
        migrationVersionRepository.save(mv);
        migrationHistoryRepository.save(MigrationHistory.from(mv.getDatabaseId(), mv.getRollback(), mv.getMigrationStatus()));
      } catch (Exception e) {
        log.error("#MIGRATION - [{}] rollback query [{}] failed to execute, ", credential.name(), mv.getRollback(), e);
        MigrationVersion.afterRollback(mv, MigrationVersion.FAILED_MIGRATION);
        migrationVersionRepository.save(mv);
        migrationHistoryRepository.save(MigrationHistory.from(mv.getDatabaseId(), mv.getRollback(), mv.getMigrationStatus()));
      } finally {
        try {
          if (statement != null) statement.close();
          if (connection != null) connection.close();
        } catch (Exception ex) {
          log.error("#MIGRATION - [{}] rollback query [{}] failed to close, ", credential.name(), mv.getMigration(), ex);
        }
      }
    }

    return MigrationResponseDTO.from(mvs);
  }

  @Override
  public SchemaResponseDTO getSchema(BaseMigrationRequestDTO request) {
    List<MigrationVersion> migrationVersions = migrationVersionRepository.findByDatabaseId(request.getDatabaseId());

    return SchemaResponseDTO.from(migrationVersions);
  }

  @Override
  public SequenceResponseDTO getLastSequence(BaseMigrationRequestDTO request) {
    MigrationVersion migrationVersion = migrationVersionRepository.findFirstByDatabaseIdOrderByIdDesc(request.getDatabaseId());
    SequenceResponseDTO response = new SequenceResponseDTO();

    if (migrationVersion == null) {
      response.setLastSequence(0);
      return response;
    }

    response.setLastSequence(migrationVersion.getId());
    return response;
  }

  private Credential getDatabaseCredential(String databaseId) {
    DatabaseCredential dbCredential = databaseCredentialRepository.findById(databaseId)
        .orElseThrow(() -> new IllegalArgumentException("databaseId not exists"));

    return new Credential(
        dbCredential.getName(),
        String.format(JDBC_FORMAT, dbCredential.getDbHost(), dbCredential.getDbName()),
        rsaUtil.decrypt(dbCredential.getDbUser()),
        rsaUtil.decryptPassword(dbCredential.getDbPassword())
    );
  }

  private record Credential(String name, String host, String user, String password) {
  }
}

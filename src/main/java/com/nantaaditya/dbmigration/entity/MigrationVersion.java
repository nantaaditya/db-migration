package com.nantaaditya.dbmigration.entity;

import com.nantaaditya.dbmigration.model.exception.InvalidParameterException;
import com.nantaaditya.dbmigration.model.request.CreateMigrationRequestDTO.MigrationRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Table(name = "migration_version")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MigrationVersion {

  public static final int CREATE_MIGRATION = 0;
  public static final int SUCCESS_MIGRATION = 1;

  public static final int FAILED_MIGRATION = 2;

  public static final int SUCCESS_ROLLBACK_MIGRATION = 3;

  public static final int FAILED_ROLLBACK_MIGRATION = 4;

  public static final Set<Integer> MIGRATION_STATUS = Set.of(SUCCESS_MIGRATION, FAILED_MIGRATION);

  public static final Set<Integer> ROLLBACK_STATUS = Set.of(SUCCESS_ROLLBACK_MIGRATION, FAILED_ROLLBACK_MIGRATION);

  @Id
  private long id;

  private String databaseId;

  @Column(name = "migration", columnDefinition = "TEXT")
  private String migration;

  @Column(name = "rollback", columnDefinition = "TEXT")
  private String rollback;

  private int migrationStatus;

  @CreatedDate
  private LocalDateTime createdDate;

  private LocalDateTime executedDate;

  private LocalDateTime rollbackDate;

  public static MigrationVersion from(String databaseId, MigrationRequest request) {
    MigrationVersion migrationVersion = new MigrationVersion();
    migrationVersion.setDatabaseId(databaseId);
    migrationVersion.setId(request.getId());
    migrationVersion.setMigration(request.getMigration());
    migrationVersion.setRollback(request.getRollback());
    migrationVersion.setMigrationStatus(CREATE_MIGRATION);
    migrationVersion.setCreatedDate(LocalDateTime.now());
    return migrationVersion;
  }

  public static MigrationVersion from(String databaseId, long id, String migrationScript) {
    MigrationVersion migrationVersion = new MigrationVersion();
    migrationVersion.setDatabaseId(databaseId);
    migrationVersion.setId(id);
    migrationVersion.setMigration(migrationScript);
    migrationVersion.setMigrationStatus(CREATE_MIGRATION);
    migrationVersion.setCreatedDate(LocalDateTime.now());
    return migrationVersion;
  }

  public static Set<MigrationVersion> from(String databaseId, Set<MigrationRequest> requests) {
    TreeSet<MigrationVersion> mvs = requests.stream()
        .map(r -> from(databaseId, r))
        .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingLong(MigrationVersion::getId))));
    return mvs;
  }

  public static void afterMigrate(MigrationVersion migrationVersion, int migrationStatus) {
    int currentStatus = migrationVersion.getMigrationStatus();
    if (currentStatus != CREATE_MIGRATION && !MIGRATION_STATUS.contains(Integer.valueOf(migrationStatus))) {
      throw new IllegalArgumentException(String.format("migration status %s not valid", migrationStatus));
    }

    migrationVersion.setMigrationStatus(migrationStatus);
    migrationVersion.setExecutedDate(LocalDateTime.now());
  }

  public static void afterRollback(MigrationVersion migrationVersion, int rollbackStatus) {
    int currentStatus = migrationVersion.getMigrationStatus();
    if (currentStatus != SUCCESS_MIGRATION && ROLLBACK_STATUS.contains(Integer.valueOf(rollbackStatus))) {
      throw new IllegalArgumentException(String.format("migration status %s not valid to rollback", rollbackStatus));
    }

    migrationVersion.setMigrationStatus(rollbackStatus);
    migrationVersion.setRollbackDate(LocalDateTime.now());
  }

  public MigrationVersion update(String rollbackScript) {
    if (this == null) new InvalidParameterException("data is null", Map.of("data", "NotNull"));

    this.setRollback(rollbackScript);
    return this;
  }

  public boolean isSuccessMigration() {
    return this.migrationStatus == SUCCESS_MIGRATION;
  }
  public boolean isValidToMigrate() {
    return this.migrationStatus == CREATE_MIGRATION;
  }

  public boolean isValidToRollback() {
    return this.migrationStatus == SUCCESS_MIGRATION;
  }
}

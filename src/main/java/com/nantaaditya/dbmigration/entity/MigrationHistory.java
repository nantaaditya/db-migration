package com.nantaaditya.dbmigration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Table(name = "migration_history")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MigrationHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long id;

  private String databaseId;

  @Column(columnDefinition = "TEXT")
  private String script;

  private int status;

  @CreatedDate
  private LocalDateTime createdDate;

  public static MigrationHistory from(String databaseId, String script, int status) {
    MigrationHistory migrationHistory = new MigrationHistory();
    migrationHistory.setDatabaseId(databaseId);
    migrationHistory.setScript(script);
    migrationHistory.setStatus(status);
    migrationHistory.setCreatedDate(LocalDateTime.now());
    return migrationHistory;
  }
}

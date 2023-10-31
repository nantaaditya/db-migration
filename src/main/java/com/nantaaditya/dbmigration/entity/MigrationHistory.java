package com.nantaaditya.dbmigration.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Data
@Entity
@Table(name = "migration_history")
@NoArgsConstructor
@EnableJpaAuditing
public class MigrationHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long id;

  private String script;

  private int status;

  @CreatedDate
  private LocalDateTime createdDate;

  public static MigrationHistory from(String script, int status) {
    MigrationHistory migrationHistory = new MigrationHistory();
    migrationHistory.setScript(script);
    migrationHistory.setStatus(status);
    migrationHistory.setCreatedDate(LocalDateTime.now());
    return migrationHistory;
  }
}

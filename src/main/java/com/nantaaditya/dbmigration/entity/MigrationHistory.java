package com.nantaaditya.dbmigration.entity;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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

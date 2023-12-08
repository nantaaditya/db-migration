package com.nantaaditya.dbmigration.entity;

import com.github.f4b6a3.tsid.TsidCreator;
import com.nantaaditya.dbmigration.model.request.BaseDatabaseCredentialRequestDTO;
import com.nantaaditya.dbmigration.model.request.UpdateDatabaseCredentialRequestDTO;
import com.nantaaditya.dbmigration.util.RSAUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "database_credentials")
@EntityListeners(AuditingEntityListener.class)
public class DatabaseCredential {

  @Id
  private String id;
  @CreatedBy
  private String createdBy;
  @CreatedDate
  private LocalDate createdDate;
  @CreatedDate
  private LocalTime createdTime;
  @LastModifiedBy
  private String modifiedBy;
  @LastModifiedDate
  private LocalDate modifiedDate;
  @LastModifiedDate
  private LocalTime modifiedTime;
  private String name;
  private String dbHost;
  private String dbName;
  @Column(columnDefinition = "TEXT")
  private String dbUser;
  @Column(columnDefinition = "TEXT")
  private String dbPassword;
  @Column(columnDefinition = "TEXT")
  private String passphrase;

  public static String createId() {
    return TsidCreator.getTsid1024().toLowerCase();
  }

  public static DatabaseCredential create(BaseDatabaseCredentialRequestDTO request, RSAUtil rsaUtil) {
    return DatabaseCredential.builder()
        .id(createId())
        .name(request.getName())
        .dbHost(request.getDbHost())
        .dbName(request.getDbName())
        .dbUser(rsaUtil.encrypt(request.getDbUser()))
        .dbPassword(rsaUtil.encryptPassword(request.getDbPassword()))
        .passphrase(rsaUtil.encrypt(request.getPassphrase()))
        .build();
  }

  public static DatabaseCredential update(UpdateDatabaseCredentialRequestDTO request, DatabaseCredential dbCredential,
      RSAUtil rsaUtil) {
    dbCredential.setName(request.getName());
    dbCredential.setDbHost(request.getDbHost());
    dbCredential.setDbName(request.getDbName());
    dbCredential.setDbUser(rsaUtil.encrypt(request.getDbUser()));
    dbCredential.setDbPassword(rsaUtil.encryptPassword(request.getDbPassword()));
    dbCredential.setPassphrase(rsaUtil.encrypt(request.getPassphrase()));
    return dbCredential;
  }
}

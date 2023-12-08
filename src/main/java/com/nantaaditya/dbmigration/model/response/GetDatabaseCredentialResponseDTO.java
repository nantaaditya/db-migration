package com.nantaaditya.dbmigration.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GetDatabaseCredentialResponseDTO extends BaseDatabaseCredentialDTO{
  private String dbHost;
  private String dbName;
  private String dbUser;
  private String dbPassword;
  private String passphrase;
}

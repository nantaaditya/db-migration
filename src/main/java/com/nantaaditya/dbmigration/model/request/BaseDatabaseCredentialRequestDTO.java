package com.nantaaditya.dbmigration.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseDatabaseCredentialRequestDTO {

  @NotNull(message = "NotNull")
  @Size(max = 255, message = "TooLong")
  private String name;
  @NotNull(message = "NotNull")
  @Size(max = 255, message = "TooLong")
  private String dbHost;
  @NotNull(message = "NotNull")
  @Size(max = 255, message = "TooLong")
  private String dbName;
  @NotNull(message = "NotNull")
  @Size(max = 255, message = "TooLong")
  private String dbUser;
  @NotNull(message = "NotNull")
  @Size(max = 255, message = "TooLong")
  private String dbPassword;
  @NotNull(message = "NotNull")
  @Size(max = 255, message = "TooLong")
  private String passphrase;
}

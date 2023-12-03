package com.nantaaditya.dbmigration.model.request;

import com.nantaaditya.dbmigration.validator.DatabaseIdMustExists;
import com.nantaaditya.dbmigration.validator.GetDatabaseCredentialMustValid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@GetDatabaseCredentialMustValid
public class GetDatabaseCredentialRequestDTO implements IGetDatabaseCredentialRequestDTO{
  @NotNull(message = "NotNull")
  @DatabaseIdMustExists
  private String databaseId;
  @NotNull(message = "NotNull")
  private String passphrase;
}

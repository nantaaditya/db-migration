package com.nantaaditya.dbmigration.model.request;

import com.nantaaditya.dbmigration.validator.DatabaseIdMustExists;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDatabaseCredentialRequestDTO extends BaseDatabaseCredentialRequestDTO {
  @NotNull(message = "NotNull")
  @DatabaseIdMustExists
  private String id;
}

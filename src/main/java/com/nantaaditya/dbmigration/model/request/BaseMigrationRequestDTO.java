package com.nantaaditya.dbmigration.model.request;

import com.nantaaditya.dbmigration.validator.DatabaseIdMustExists;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BaseMigrationRequestDTO {
  @NotNull(message = "NotNull")
  @DatabaseIdMustExists
  private String databaseId;
}

package com.nantaaditya.dbmigration.model.request;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RollbackMigrationRequestDTO extends BaseMigrationRequestDTO{

  @Min(value = 0, message = "NotValid")
  private long version;
}

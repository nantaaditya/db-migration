package com.nantaaditya.dbmigration.model;

import com.nantaaditya.dbmigration.validator.IdMustValid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MigrationRequestDTO {
  @Min(value = 1, message = "must positive")
  @IdMustValid
  private long id;
  @NotBlank(message = "must not blank")
  private String migration;
  @NotBlank(message = "must not blank")
  private String rollback;
}

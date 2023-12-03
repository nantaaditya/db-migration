package com.nantaaditya.dbmigration.model.request;

import com.nantaaditya.dbmigration.validator.MigrationIdMustValid;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.beans.Transient;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@MigrationIdMustValid
public class CreateMigrationRequestDTO extends BaseMigrationRequestDTO implements ICreateMigrationRequestDTO{
  @Valid
  @NotEmpty(message = "NotEmpty")
  private Set<MigrationRequest> migrations;

  @Override
  @Transient
  public Set<Long> getIds() {
    return migrations.stream()
        .map(MigrationRequest::getId)
        .collect(Collectors.toSet());
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class MigrationRequest {
    @Min(value = 1, message = "MustPositive")
    private long id;
    @NotNull(message = "NotNull")
    private String migration;
    @NotNull(message = "NotNull")
    private String rollback;
  }
}

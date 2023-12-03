package com.nantaaditya.dbmigration.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseDatabaseCredentialDTO {
  private String id;
  private String name;
}

package com.nantaaditya.dbmigration.model.request;

import java.util.Set;

public interface ICreateMigrationRequestDTO {
  String getDatabaseId();

  Set<Long> getIds();
}

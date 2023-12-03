package com.nantaaditya.dbmigration.model.request;

import java.util.Set;

public interface IGetDatabaseCredentialRequestDTO {
  String getDatabaseId();

  String getPassphrase();
}

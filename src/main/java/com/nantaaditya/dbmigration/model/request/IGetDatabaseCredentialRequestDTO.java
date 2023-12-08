package com.nantaaditya.dbmigration.model.request;

public interface IGetDatabaseCredentialRequestDTO {
  String getDatabaseId();

  String getPassphrase();
}

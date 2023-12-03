package com.nantaaditya.dbmigration.repository;

import com.nantaaditya.dbmigration.entity.DatabaseCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatabaseCredentialRepository extends JpaRepository<DatabaseCredential, String> {
}

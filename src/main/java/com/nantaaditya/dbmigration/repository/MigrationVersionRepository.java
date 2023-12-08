package com.nantaaditya.dbmigration.repository;

import com.nantaaditya.dbmigration.entity.MigrationVersion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MigrationVersionRepository extends JpaRepository<MigrationVersion, Long> {

  List<MigrationVersion> findByDatabaseId(String databaseId);

  List<MigrationVersion> findByDatabaseIdAndIdAfterAndMigrationStatusAndRollbackDateIsNullOrderByIdDesc(String databaseId, long id, int migrationStatus);

  MigrationVersion findFirstByDatabaseIdOrderByIdDesc(String databaseId);
}

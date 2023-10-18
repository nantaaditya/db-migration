package com.nantaaditya.dbmigration.repository;

import com.nantaaditya.dbmigration.entity.MigrationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MigrationHistoryRepository extends JpaRepository<MigrationHistory, Long> {

}

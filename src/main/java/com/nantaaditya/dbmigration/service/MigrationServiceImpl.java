package com.nantaaditya.dbmigration.service;

import com.nantaaditya.dbmigration.entity.DatabaseCredential;
import com.nantaaditya.dbmigration.entity.MigrationHistory;
import com.nantaaditya.dbmigration.entity.MigrationVersion;
import com.nantaaditya.dbmigration.model.exception.InvalidParameterException;
import com.nantaaditya.dbmigration.model.request.BaseMigrationRequestDTO;
import com.nantaaditya.dbmigration.model.request.CreateMigrationRequestDTO;
import com.nantaaditya.dbmigration.model.request.RollbackMigrationRequestDTO;
import com.nantaaditya.dbmigration.model.response.MigrationResponseDTO;
import com.nantaaditya.dbmigration.model.response.SchemaResponseDTO;
import com.nantaaditya.dbmigration.model.response.SequenceResponseDTO;
import com.nantaaditya.dbmigration.properties.FilePathProperties;
import com.nantaaditya.dbmigration.repository.DatabaseCredentialRepository;
import com.nantaaditya.dbmigration.repository.MigrationHistoryRepository;
import com.nantaaditya.dbmigration.repository.MigrationVersionRepository;
import com.nantaaditya.dbmigration.util.RSAUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class MigrationServiceImpl implements MigrationService {

  @Autowired
  private MigrationVersionRepository migrationVersionRepository;

  @Autowired
  private MigrationHistoryRepository migrationHistoryRepository;

  @Autowired
  private DatabaseCredentialRepository databaseCredentialRepository;

  @Autowired
  private RSAUtil rsaUtil;

  @Autowired
  private FilePathProperties filePathProperties;

  private static final String JDBC_FORMAT = "jdbc:postgresql://%s/%s";

  @Override
  public Set<MigrationResponseDTO> runMigration(CreateMigrationRequestDTO request) {
    MigrationVersion[] migrationVersions = migrationVersionRepository.saveAll(
          MigrationVersion.from(request.getDatabaseId(), request.getMigrations())
        )
        .stream()
        .sorted(Comparator.comparingLong(MigrationVersion::getId))
        .toArray(MigrationVersion[]::new);

    Credential credential = getDatabaseCredential(request.getDatabaseId());

    Connection connection = null;
    Statement statement = null;
    int result = 0;


    for (int i=0; i < migrationVersions.length; i++) {
      MigrationVersion mv = migrationVersions[i];
      try {
        connection = DriverManager.getConnection(credential.host(), credential.user(), credential.password());
        statement = connection.createStatement();
        result = statement.executeUpdate(mv.getMigration());
        log.info("#MIGRATION - [{}] query [{}] result [{}]", credential.name(), mv.getMigration(), result);

        if (mv.isValidToMigrate()) {
          MigrationVersion.afterMigrate(mv, MigrationVersion.SUCCESS_MIGRATION);
        } else {
          MigrationVersion.afterMigrate(mv, MigrationVersion.FAILED_MIGRATION);
        }
        migrationVersionRepository.save(mv);
        migrationHistoryRepository.save(MigrationHistory.from(mv.getDatabaseId(), mv.getMigration(), mv.getMigrationStatus()));
      } catch (Throwable e) {
        log.error("#MIGRATION - [{}] query [{}] failed to execute, ", credential.name(), mv.getMigration(), e);
        MigrationVersion.afterMigrate(mv, MigrationVersion.FAILED_MIGRATION);
        migrationVersionRepository.save(mv);
        migrationHistoryRepository.save(MigrationHistory.from(credential.name(), mv.getMigration(), mv.getMigrationStatus()));
      } finally {
        try {
          if (statement != null) statement.close();
          if (connection != null) connection.close();
        } catch (Exception ex) {
          log.error("#MIGRATION - [{}] query [{}] failed to close, ", credential.name(), mv.getMigration(), ex);
        }
      }
    }

    return MigrationResponseDTO.from(migrationVersions);
  }

  @Override
  public Set<MigrationResponseDTO> uploadMigration(String databaseId, MultipartFile migrationFile,
      MultipartFile rollbackFile) {
    Credential credential = getDatabaseCredential(databaseId);

    if (migrationFile.isEmpty()) throw new InvalidParameterException("migration file is empty", Map.of("migrationFile", "NotNull"));
    if (rollbackFile.isEmpty()) throw new InvalidParameterException("rollback file is empty", Map.of("rollbackFile", "NotNull"));

    if (isFileExtensionValid(migrationFile, "sql")) throw new InvalidParameterException("migration file is not valid", Map.of("migrationFile", "NotValid"));
    if (isFileExtensionValid(rollbackFile, "sql")) throw new InvalidParameterException("rollback file is not valid", Map.of("rollbackFile", "NotValid"));

    writeFile(migrationFile);
    writeFile(rollbackFile);

    File migrationDDLFile = loadFile(migrationFile.getOriginalFilename());
    File rollbackDDLFile = loadFile(rollbackFile.getOriginalFilename());

    Set<MigrationVersion> migrationVersions = new HashSet<>();
    MigrationVersion migrationVersion = migrationVersionRepository.findFirstByDatabaseIdOrderByIdDesc(databaseId);

    readAndComposeMigration(migrationDDLFile, migrationVersion.getId(), (String migrationScript, Long counter) -> {
      MigrationVersion mv = MigrationVersion.from(databaseId, counter, migrationScript);
      migrationVersions.add(mv);
      return migrationVersions;
    });

    readAndComposeMigration(rollbackDDLFile, migrationVersion.getId(), (String rollbackScript, Long counter) -> {
      MigrationVersion mv = retrieve(migrationVersions, counter);
      mv.setRollback(rollbackScript);
      remove(migrationVersions, counter);
      migrationVersions.add(mv);
      return migrationVersions;
    });

    Connection connection = null;
    Statement statement = null;
    int result = 0;

    MigrationVersion[] mvs = migrationVersions.stream()
        .sorted(Comparator.comparingLong(MigrationVersion::getId))
        .toArray(MigrationVersion[]::new);

    for (int i=0; i < mvs.length; i++) {
      MigrationVersion mv = mvs[i];
      try {
        connection = DriverManager.getConnection(credential.host(), credential.user(), credential.password());
        statement = connection.createStatement();
        result = statement.executeUpdate(mv.getMigration());
        log.info("#MIGRATION - [{}] query [{}] result [{}]", credential.name(), mv.getMigration(), result);

        if (mv.isValidToMigrate()) {
          MigrationVersion.afterMigrate(mv, MigrationVersion.SUCCESS_MIGRATION);
        } else {
          MigrationVersion.afterMigrate(mv, MigrationVersion.FAILED_MIGRATION);
        }
        migrationVersionRepository.save(mv);
        migrationHistoryRepository.save(MigrationHistory.from(mv.getDatabaseId(), mv.getMigration(), mv.getMigrationStatus()));
      } catch (Throwable e) {
        log.error("#MIGRATION - [{}] query [{}] failed to execute, ", credential.name(), mv.getMigration(), e);
        MigrationVersion.afterMigrate(mv, MigrationVersion.FAILED_MIGRATION);
        migrationVersionRepository.save(mv);
        migrationHistoryRepository.save(MigrationHistory.from(credential.name(), mv.getMigration(), mv.getMigrationStatus()));
      } finally {
        try {
          if (statement != null) statement.close();
          if (connection != null) connection.close();
        } catch (Exception ex) {
          log.error("#MIGRATION - [{}] query [{}] failed to close, ", credential.name(), mv.getMigration(), ex);
        }
      }
    }

    return MigrationResponseDTO.from(mvs);
  }

  @Override
  public Set<MigrationResponseDTO> runRollback(RollbackMigrationRequestDTO request) {
    List<MigrationVersion> migrationVersions = migrationVersionRepository
        .findByDatabaseIdAndIdAfterAndMigrationStatusAndRollbackDateIsNullOrderByIdDesc(
            request.getDatabaseId(), request.getVersion(), MigrationVersion.SUCCESS_MIGRATION);

    if (migrationVersions.isEmpty()) return new HashSet<>();

    Credential credential = getDatabaseCredential(request.getDatabaseId());

    Connection connection = null;
    Statement statement = null;
    int result = 0;

    MigrationVersion[] mvs = migrationVersions
        .stream()
        .sorted(Comparator.comparingLong(MigrationVersion::getId).reversed())
        .toArray(MigrationVersion[]::new);


    for (int i=0; i< mvs.length; i++) {
      MigrationVersion mv = mvs[i];
      try {
        connection = DriverManager.getConnection(credential.host(), credential.user(), credential.password());
        statement = connection.createStatement();
        result = statement.executeUpdate(mv.getRollback());

        log.info("#MIGRATION - [{}] rollback query [{}] result [{}]", credential.name(), mv.getRollback(), result);
        MigrationVersion.afterRollback(mv, MigrationVersion.SUCCESS_ROLLBACK_MIGRATION);
        migrationVersionRepository.save(mv);
        migrationHistoryRepository.save(MigrationHistory.from(mv.getDatabaseId(), mv.getRollback(), mv.getMigrationStatus()));
      } catch (Exception e) {
        log.error("#MIGRATION - [{}] rollback query [{}] failed to execute, ", credential.name(), mv.getRollback(), e);
        MigrationVersion.afterRollback(mv, MigrationVersion.FAILED_MIGRATION);
        migrationVersionRepository.save(mv);
        migrationHistoryRepository.save(MigrationHistory.from(mv.getDatabaseId(), mv.getRollback(), mv.getMigrationStatus()));
      } finally {
        try {
          if (statement != null) statement.close();
          if (connection != null) connection.close();
        } catch (Exception ex) {
          log.error("#MIGRATION - [{}] rollback query [{}] failed to close, ", credential.name(), mv.getMigration(), ex);
        }
      }
    }

    return MigrationResponseDTO.from(mvs);
  }

  @Override
  public SchemaResponseDTO getSchema(BaseMigrationRequestDTO request) {
    List<MigrationVersion> migrationVersions = migrationVersionRepository.findByDatabaseId(request.getDatabaseId());

    return SchemaResponseDTO.from(migrationVersions);
  }

  @Override
  public SequenceResponseDTO getLastSequence(BaseMigrationRequestDTO request) {
    MigrationVersion migrationVersion = migrationVersionRepository.findFirstByDatabaseIdOrderByIdDesc(request.getDatabaseId());
    SequenceResponseDTO response = new SequenceResponseDTO();

    if (migrationVersion == null) {
      response.setLastSequence(0);
      return response;
    }

    response.setLastSequence(migrationVersion.getId());
    return response;
  }

  private Credential getDatabaseCredential(String databaseId) {
    DatabaseCredential dbCredential = databaseCredentialRepository.findById(databaseId)
        .orElseThrow(() -> new InvalidParameterException("databaseId not exists", Map.of("databaseId", "NotExists")));

    return new Credential(
        dbCredential.getName(),
        String.format(JDBC_FORMAT, dbCredential.getDbHost(), dbCredential.getDbName()),
        rsaUtil.decrypt(dbCredential.getDbUser()),
        rsaUtil.decryptPassword(dbCredential.getDbPassword())
    );
  }

  private void writeFile(MultipartFile multipartFile) {
    Path root = Paths.get(filePathProperties.getUploadPath());
    try {
      Files.copy(multipartFile.getInputStream(), root.resolve(multipartFile.getOriginalFilename()));
    } catch (Exception ex) {
      log.error("#MIGRATION - failed to write file {}, ", multipartFile.getOriginalFilename(), ex);
      throw new InvalidParameterException("failed to write file", Map.of(multipartFile.getOriginalFilename(), "NotValid"));
    }
  }

  private File loadFile(String fileName) {
    Path root = Paths.get(filePathProperties.getUploadPath() + "/" + fileName);
    try {
      Resource resource = new UrlResource(root.toUri());
      if (resource.exists() || resource.isReadable()) return resource.getFile();

      throw new InvalidParameterException("failed to read file", Map.of(fileName, "NotValid"));
    } catch (Exception ex) {
      log.error("#MIGRATION - failed to read file {}, ", fileName, ex);
      throw new InvalidParameterException("failed to write file", Map.of(fileName, "NotValid"));
    }
  }

  private void readAndComposeMigration(File migrationFile, long counter,
      BiFunction<String, Long, Set<MigrationVersion>> function) {

    StringBuilder sb = new StringBuilder();
    try (FileReader migrationReader = new FileReader(migrationFile);
        BufferedReader migrationBufferedReader = new BufferedReader(migrationReader)){

      String migrationLine = null;

      while ((migrationLine = migrationBufferedReader.readLine()) != null) {
        migrationLine = migrationLine.trim();

        if (migrationLine.isEmpty() || migrationLine.startsWith("--")) continue;

        sb.append(migrationLine);

        if (migrationLine.endsWith(";")) {
         function.apply(sb.toString(), ++counter);
        }
        sb.setLength(0);
      }
    } catch (Exception ex) {
      log.error("#MIGRATION - failed to add migration script, ", ex);
      sb.setLength(0);
    }
  }

  public MigrationVersion retrieve(Set<MigrationVersion> migrationVersions, long id) {
    return migrationVersions.stream()
        .filter(mv -> mv.getId() == id)
        .findFirst()
        .orElse(null);
  }

  public void remove(Set<MigrationVersion> migrationVersions, long id) {
    for (Iterator<MigrationVersion> iterator = migrationVersions.iterator(); iterator.hasNext();) {
      MigrationVersion mv = iterator.next();
      if (mv.getId() == id) {
        iterator.remove();
      }
    }
  }

  public boolean isFileExtensionValid(MultipartFile file, String extension) {
    return Optional.ofNullable(file)
        .map(MultipartFile::getOriginalFilename)
        .filter(f -> f.contains("."))
        .map(f -> f.substring(f.lastIndexOf(".") + 1))
        .stream()
        .anyMatch(fileExtension -> extension.equals(fileExtension));
  }

  private record Credential(String name, String host, String user, String password) {
  }
}

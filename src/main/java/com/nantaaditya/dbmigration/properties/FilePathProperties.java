package com.nantaaditya.dbmigration.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "file.configuration")
public class FilePathProperties {
  private String uploadPath;
}

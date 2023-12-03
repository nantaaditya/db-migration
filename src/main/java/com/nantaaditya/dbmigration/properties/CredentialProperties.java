package com.nantaaditya.dbmigration.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "credential.configuration")
public class CredentialProperties {
  private String privateKey;
  private String publicKey;
  private String prefix;
  private String postfix;
}

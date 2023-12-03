package com.nantaaditya.dbmigration;

import com.nantaaditya.dbmigration.properties.CredentialProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(value = CredentialProperties.class)
public class DbMigrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbMigrationApplication.class, args);
	}

}

package com.nantaaditya.dbmigration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DbMigrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbMigrationApplication.class, args);
	}

}

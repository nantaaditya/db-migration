package com.nantaaditya.dbmigration.configuration;

import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfiguration {
  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.ant("/api/**"))
        .build()
        .apiInfo(new ApiInfo(
            "DB Migration",
            "DDL DB Migration for PostgreSQL",
            "0.0.1",
            null,
            new Contact(
                "Nanta Aditya",
                "https://nantaaditya.com",
                "personal@nantaaditya.com"
            ),
            "GNU AGPLv3",
            "https://www.gnu.org/licenses/agpl-3.0.en.html",
            Collections.emptyList()
            )
        );
  }
}

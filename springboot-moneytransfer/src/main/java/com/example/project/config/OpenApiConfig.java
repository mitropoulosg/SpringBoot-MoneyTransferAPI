package com.example.project.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Money Transfer API")
                        .version("1.0")
                        .description("An API performing bank transfers.")
                        .contact(new Contact()
                            .name("George")
                            .url("https://github.com/mitropoulosg")
                            .email("mitropoulos02@gmail.com")));
    }
}

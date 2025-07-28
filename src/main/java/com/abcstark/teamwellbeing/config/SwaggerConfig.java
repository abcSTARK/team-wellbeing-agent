package com.abcstark.teamwellbeing.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Swagger/OpenAPI documentation.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI teamWellbeingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Team Wellbeing Agent API")
                        .description("REST API for monitoring team health via Slack, GitHub, and Jira integrations")
                        .version("0.1.0")
                        .contact(new Contact()
                                .name("ABC STARK")
                                .email("support@abcstark.com")));
    }
}
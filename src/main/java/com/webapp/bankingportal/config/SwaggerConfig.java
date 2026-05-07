package com.webapp.bankingportal.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import lombok.val;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) configuration for the banking portal.
 *
 * <p>Registers the API metadata and configures a Bearer JWT security scheme
 * so that the Swagger UI allows developers to authenticate and test secured endpoints
 * directly from the browser.</p>
 */
@Configuration
public class SwaggerConfig {

    /**
     * Creates the {@link OpenAPI} bean with API title, description, version,
     * and a global Bearer token security requirement.
     *
     * @return the configured {@link OpenAPI} definition
     */
    @Bean
    public OpenAPI customOpenAPI() {
        val securitySchemeName = "Bearer";
        return new OpenAPI()
                .info(new Info().title("Banking Portal API")
                        .description("This is auth service use for validate the user.")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("BankingPortal-API Wiki Documentation")
                        .url("https://github.com/abhi9720/BankingPortal-API/wiki"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")));
    }

}

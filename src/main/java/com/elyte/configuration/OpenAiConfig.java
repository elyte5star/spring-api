package com.elyte.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class OpenAiConfig {
        private final String moduleName;
        private final String apiVersion;
        private final String BEARER_AUTH = "bearerAuth";
        private final String email = "checkuti@gmail.com";
        private final String name = "Utimore Services AS";
        private final String url = "https://github.com/elyte5star";
       

        public OpenAiConfig(
                        @Value("${api.module-name}") String moduleName,
                        @Value("${api.version}") String apiVersion) {
                this.moduleName = moduleName;
                this.apiVersion = apiVersion;
        }

        @Bean
        OpenAPI customOpenAPI() {
                return new OpenAPI()
                                // .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                                .components(new Components().securitySchemes(schemes()))
                                .info(apiInfo());

        }

        private Map<String, SecurityScheme> schemes() {
                SecurityScheme bearerScheme = new SecurityScheme().name(BEARER_AUTH).scheme("bearer")
                                .type(SecurityScheme.Type.HTTP).bearerFormat("JWT");
                return Map.of(BEARER_AUTH, bearerScheme);

        }

        private Info apiInfo() {
                String apiTitle = String.format("%s API.", StringUtils.capitalize(moduleName));
                return new Info().title(apiTitle).version(apiVersion)
                                .summary("Interactive Documentation for e-Market")
                                .contact(new Contact().name(name).email(email).url(url))
                                .description("This is a sample spring boot server for a web store.")
                                .license(new License().name("Proprietary")
                                                .url("https://github.com/elyte5star/spring-api"));
        }

}

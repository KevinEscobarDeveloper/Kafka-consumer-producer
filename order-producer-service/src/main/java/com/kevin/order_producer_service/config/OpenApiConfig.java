package com.kevin.order_producer_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${swagger.ui.enabled: false}")
    private boolean isEnabled;

    @Value("${swagger.info.name: Nombre de la API}")
    private String apiName;

    @Value("${swagger.info.description: Descripción de la API}")
    private String apiDescription;

    @Value("${swagger.info.version: Versión de la API}")
    private String apiVersion;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(apiName)
                        .version(apiVersion)
                        .description(apiDescription));
    }

}

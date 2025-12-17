package com.restaurant.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI restaurantPortalOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8082");
        localServer.setDescription("Local Development Server");

        Server productionServer = new Server();
        productionServer.setUrl("https://api.restaurant.com/portal");
        productionServer.setDescription("Production Server");

        return new OpenAPI()
                .servers(List.of(localServer, productionServer))
                .info(new Info()
                        .title("Restaurant Backend Portal API")
                        .description("""
                                User Portal API documentation for Restaurant Backend System.
                                
                                This API provides endpoints for customer-facing operations including:
                                - Public Information
                                - User Services
                                - Restaurant Features
                                
                                **Note:** This API is accessible to all users.
                                """)
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Restaurant Development Team")
                                .email("support@restaurant.com")
                                .url("https://restaurant.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }

}


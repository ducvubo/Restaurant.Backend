package com.restaurant.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SwaggerUiConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve custom CSS and JS files
        registry.addResourceHandler("/api/**")
                .addResourceLocations("classpath:/static/api/");
        
        // Ensure static resources are cached properly
        registry.addResourceHandler("/api/custom.js")
                .addResourceLocations("classpath:/static/api/")
                .setCachePeriod(0); // No cache for development
        
        registry.addResourceHandler("/api/custom.css")
                .addResourceLocations("classpath:/static/api/")
                .setCachePeriod(0); // No cache for development
    }
}


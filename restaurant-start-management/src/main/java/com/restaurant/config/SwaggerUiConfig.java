package com.restaurant.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Order(1) // Đảm bảo được load trước các config khác
public class SwaggerUiConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve custom CSS and JS files với priority cao
        registry.addResourceHandler("/api/custom.js")
                .addResourceLocations("classpath:/static/api/")
                .setCachePeriod(0) // No cache for development
                .resourceChain(false);
        
        registry.addResourceHandler("/api/custom.css")
                .addResourceLocations("classpath:/static/api/")
                .setCachePeriod(0) // No cache for development
                .resourceChain(false);
        
        // Serve các file static khác trong thư mục api
        registry.addResourceHandler("/api/**")
                .addResourceLocations("classpath:/static/api/")
                .setCachePeriod(0)
                .resourceChain(false);
    }
}


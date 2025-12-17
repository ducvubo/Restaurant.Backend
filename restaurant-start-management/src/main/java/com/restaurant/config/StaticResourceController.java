package com.restaurant.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Controller để serve static resources cho Swagger UI
 * Fallback nếu ResourceHandler không hoạt động
 */
@Slf4j
@RestController
public class StaticResourceController {

    @GetMapping(value = "/api/custom.js", produces = "application/javascript")
    public ResponseEntity<String> getCustomJs() {
        try {
            Resource resource = new ClassPathResource("static/api/custom.js");
            if (!resource.exists()) {
                log.warn("Custom JS file not found at: static/api/custom.js");
                return ResponseEntity.notFound().build();
            }
            String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            log.info("Serving custom.js, length: {}", content.length());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "application/javascript; charset=UTF-8")
                    .body(content);
        } catch (IOException e) {
            log.error("Error reading custom.js", e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/api/custom.css", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getCustomCss() {
        try {
            Resource resource = new ClassPathResource("static/api/custom.css");
            if (!resource.exists()) {
                log.warn("Custom CSS file not found at: static/api/custom.css");
                return ResponseEntity.notFound().build();
            }
            String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            log.info("Serving custom.css, length: {}", content.length());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "text/css; charset=UTF-8")
                    .body(content);
        } catch (IOException e) {
            log.error("Error reading custom.css", e);
            return ResponseEntity.notFound().build();
        }
    }

}


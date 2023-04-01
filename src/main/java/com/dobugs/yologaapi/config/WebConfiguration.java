package com.dobugs.yologaapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        allowLocalhostForTest(registry);
        allowGlobal(registry);
    }

    private void allowLocalhostForTest(final CorsRegistry registry) {
        registry.addMapping("/api/vi/test/**")
            .allowedOrigins("http://localhost");
    }

    private void allowGlobal(final CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .maxAge(3000);
    }
}

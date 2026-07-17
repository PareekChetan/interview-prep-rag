package com.chetan.interviewprep.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// By default, browsers block a web page from calling an API running on a
// different "origin" (different protocol/host/port) unless the server
// explicitly allows it - this is called CORS (Cross-Origin Resource Sharing).
// Since our frontend is a plain HTML file calling this API at localhost:8080,
// we need to explicitly permit that here.
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}

package com.lili.springboot.webapp.exchange_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

/**
 * General application configuration
 */
@Configuration
public class ApplicationConfig {
    /**
     * Configure Jackson ObjectMapper for JSON processing
     * This bean will be used automatically by Spring for JSON serialization/deserialization
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Configure to handle different JSON naming conventions
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        
        // Don't fail on unknown properties (APIs might return extra fields)
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        return mapper;
    }
}

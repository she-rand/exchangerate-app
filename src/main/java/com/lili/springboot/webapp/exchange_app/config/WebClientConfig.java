package com.lili.springboot.webapp.exchange_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
/**
 * Configuration class for WebClient beans
 * 
 * @Configuration tells Spring this class contains bean definitions
 * @Bean methods create Spring-managed objects that can be injected elsewhere
 */
@Configuration
public class WebClientConfig {

    /**
     * Creates a WebClient.Builder bean that can be injected into our API clients
     * WebClient is Spring's modern HTTP client (replaces RestTemplate)
     * 
     * Why WebClient over RestTemplate?
     * - Non-blocking/reactive
     * - Better error handling
     * - More modern and actively maintained
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024)) // 1MB buffer
            .build()
            .mutate(); // Returns a builder for customization in each service
    }

}

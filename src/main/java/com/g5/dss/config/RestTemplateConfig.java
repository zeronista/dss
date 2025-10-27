package com.g5.dss.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for RestTemplate and HTTP clients
 */
@Configuration
public class RestTemplateConfig {
    
    /**
     * Create RestTemplate bean for HTTP requests
     * Used for calling external services (e.g., Model Service)
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000); // 30 seconds
        factory.setReadTimeout(30000);    // 30 seconds
        
        return new RestTemplate(factory);
    }
}

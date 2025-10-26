package com.g5.dss.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.g5.dss.repository.jpa")
@EntityScan(basePackages = "com.g5.dss.domain.jpa")
@EnableTransactionManagement
public class JpaConfig {
    // Spring Boot auto-configuration will handle JPA/MySQL setup
    // Configuration can be done via application.yml
}

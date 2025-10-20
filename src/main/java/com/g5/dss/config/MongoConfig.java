package com.g5.dss.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.g5.dss.repository")
public class MongoConfig {
    // Spring Boot auto-configuration will handle MongoDB setup
    // Configuration can be done via application.yml
}


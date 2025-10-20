package com.g5.dss.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "rule_cache")
public class RuleCache {
    
    @Id
    private String id;
    private String ruleType; // "marketing", "association", etc.
    private Map<String, Object> ruleData;
    private LocalDateTime generatedAt;
    private LocalDateTime expiresAt;

    // Constructors
    public RuleCache() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRuleType() { return ruleType; }
    public void setRuleType(String ruleType) { this.ruleType = ruleType; }

    public Map<String, Object> getRuleData() { return ruleData; }
    public void setRuleData(Map<String, Object> ruleData) { this.ruleData = ruleData; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}


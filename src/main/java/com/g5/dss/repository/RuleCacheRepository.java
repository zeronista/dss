package com.g5.dss.repository;

import com.g5.dss.domain.RuleCache;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RuleCacheRepository extends MongoRepository<RuleCache, String> {
    
    Optional<RuleCache> findByRuleType(String ruleType);
    
    List<RuleCache> findByExpiresAtBefore(LocalDateTime dateTime);
}


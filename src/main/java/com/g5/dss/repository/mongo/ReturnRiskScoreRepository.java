package com.g5.dss.repository.mongo;

import com.g5.dss.domain.mongo.ReturnRiskScore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Return Risk Scores
 */
@Repository
public interface ReturnRiskScoreRepository extends MongoRepository<ReturnRiskScore, String> {
    
    /**
     * Find risk score by order ID
     */
    Optional<ReturnRiskScore> findByOrderId(String orderId);
    
    /**
     * Find all risk scores for a customer
     */
    List<ReturnRiskScore> findByCustomerId(String customerId);
    
    /**
     * Find risk scores by risk level
     */
    List<ReturnRiskScore> findByRiskLevel(String riskLevel);
    
    /**
     * Find risk scores above a certain threshold
     */
    List<ReturnRiskScore> findByRiskScoreGreaterThanEqual(Double minScore);
    
    /**
     * Find risk scores in a date range
     */
    List<ReturnRiskScore> findByScoredAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Find risk scores by country
     */
    List<ReturnRiskScore> findByCountry(String country);
    
    /**
     * Find risk scores by policy ID
     */
    List<ReturnRiskScore> findByPolicyId(String policyId);
    
    /**
     * Count orders by risk level
     */
    Long countByRiskLevel(String riskLevel);
    
    /**
     * Count orders with actual returns
     */
    Long countByActualReturn(Boolean actualReturn);
}

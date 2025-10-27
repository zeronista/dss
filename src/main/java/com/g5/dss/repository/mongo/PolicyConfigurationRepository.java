package com.g5.dss.repository.mongo;

import com.g5.dss.domain.mongo.PolicyConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Policy Configurations
 */
@Repository
public interface PolicyConfigurationRepository extends MongoRepository<PolicyConfiguration, String> {
    
    /**
     * Find policy by name
     */
    Optional<PolicyConfiguration> findByPolicyName(String policyName);
    
    /**
     * Find active policies
     */
    List<PolicyConfiguration> findByIsActiveTrue();
    
    /**
     * Find default policy
     */
    Optional<PolicyConfiguration> findByIsDefaultTrue();
    
    /**
     * Find policies by country
     */
    List<PolicyConfiguration> findByCountry(String country);
    
    /**
     * Find global policies (country = null)
     */
    List<PolicyConfiguration> findByCountryIsNull();
    
    /**
     * Find policies created by user
     */
    List<PolicyConfiguration> findByCreatedBy(String createdBy);
}

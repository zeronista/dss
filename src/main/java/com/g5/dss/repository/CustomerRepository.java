package com.g5.dss.repository;

import com.g5.dss.domain.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
    
    Optional<Customer> findByCustomerId(String customerId);
    
    Optional<Customer> findByEmail(String email);
    
    List<Customer> findBySegment(String segment);
}


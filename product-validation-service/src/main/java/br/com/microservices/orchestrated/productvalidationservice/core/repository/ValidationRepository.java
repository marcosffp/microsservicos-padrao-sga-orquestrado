package br.com.microservices.orchestrated.productvalidationservice.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.microservices.orchestrated.productvalidationservice.core.model.Validation;

@Repository
public interface ValidationRepository extends JpaRepository<Validation, Integer> {

  Boolean existsByOrderIdAndTransactionId(String orderId, String transactionId);

  Optional<Validation> findByOrderIdAndTransactionId(String orderId, String transactionId);

}
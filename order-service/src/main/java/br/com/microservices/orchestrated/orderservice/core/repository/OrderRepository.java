package br.com.microservices.orchestrated.orderservice.core.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.microservices.orchestrated.orderservice.core.document.Order;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
} 

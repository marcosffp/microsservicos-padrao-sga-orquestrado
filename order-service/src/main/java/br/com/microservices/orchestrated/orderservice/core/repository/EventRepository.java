package br.com.microservices.orchestrated.orderservice.core.repository;


import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;

import br.com.microservices.orchestrated.orderservice.core.document.Event;


@Repository
public interface EventRepository extends MongoRepository<Event, String> {

  Optional<Event> findTop1ByOrderIdOrderByCreatedAtDesc(String orderId);

  Optional<Event> findTop1ByTransactionIdOrderByCreatedAtDesc(String transactionId);
}

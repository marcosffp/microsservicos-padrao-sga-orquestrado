package br.com.microservices.orchestrated.orderservice.core.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.microservices.orchestrated.orderservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.orderservice.core.document.Event;
import br.com.microservices.orchestrated.orderservice.core.dto.EventFilters;
import br.com.microservices.orchestrated.orderservice.core.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EventService {
  @Autowired
  private EventRepository eventRepository;

  public void notifyEnding(Event event) {
    event.setOrderId(event.getOrderId());
    event.setCreatedAt(LocalDateTime.now());
    save(event);
    log.info("Order {} with saga notifiel! Transaction ID: {}", event.getOrderId(), event.getTransactionId());
  }

  public Event save(Event event) {
    return eventRepository.save(event);
  }

  public Event findByFilters(EventFilters filters) {
    validateEmptyFilters(filters);
    if (!filters.getOrderId().isEmpty()) {
      return findByOrderId(filters.getOrderId());
    } else {
      return findByTransactionId(filters.getTransactionId());
    }
  }

  private void validateEmptyFilters(EventFilters filters) {
    if (filters.getOrderId().isEmpty() && filters.getTransactionId().isEmpty()) {
      throw new ValidationException("OrderID or TransactionID must be informed.");
    }
  }

  private Event findByTransactionId(String transactionId) {
    return eventRepository
        .findTop1ByTransactionIdOrderByCreatedAtDesc(transactionId)
        .orElseThrow(() -> new ValidationException("Event not found by transactionId."));
  }

  private Event findByOrderId(String orderId) {
    return eventRepository
        .findTop1ByOrderIdOrderByCreatedAtDesc(orderId)
        .orElseThrow(() -> new ValidationException("Event not found by orderID."));
  }

  public List<Event> findAll() {
  return eventRepository.findAll();
}


}

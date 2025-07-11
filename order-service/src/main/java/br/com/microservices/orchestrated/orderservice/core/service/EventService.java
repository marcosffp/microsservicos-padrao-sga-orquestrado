package br.com.microservices.orchestrated.orderservice.core.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import br.com.microservices.orchestrated.orderservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.orderservice.core.document.Event;
import br.com.microservices.orchestrated.orderservice.core.dto.EventFilters;
import br.com.microservices.orchestrated.orderservice.core.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
  
  private final EventRepository eventRepository;

  public void notifyEnding(Event event) {
    event.setCreatedAt(LocalDateTime.now());
    save(event);
    log.info("Pedido {} com saga notificado! ID da transação: {}", event.getOrderId(), event.getTransactionId());
  }

  public Event save(Event event) {
    return eventRepository.save(event);
  }

  public Event findByFilters(EventFilters filters) {
    if (isEmpty(filters.orderId()) && isEmpty(filters.transactionId())) {
      throw new ValidationException("OrderID ou TransactionID devem ser informados.");
    }
    if (!isEmpty(filters.orderId())) {
      return findByOrderId(filters.orderId());
    }
    return findByTransactionId(filters.transactionId());
  }

  private boolean isEmpty(String value) {
    return value == null || value.isEmpty();
  }

  private Event findByTransactionId(String transactionId) {
    return eventRepository.findTop1ByTransactionIdOrderByCreatedAtDesc(transactionId)
        .orElseThrow(() -> new ValidationException("Evento não encontrado pelo transactionId."));
  }

  private Event findByOrderId(String orderId) {
    return eventRepository.findTop1ByOrderIdOrderByCreatedAtDesc(orderId)
        .orElseThrow(() -> new ValidationException("Evento não encontrado pelo orderID."));
  }

  public List<Event> findAll() {
    return eventRepository.findAll();
  }
}

package br.com.microservices.orchestrated.inventoryservice.core.service;

import org.springframework.stereotype.Component;

import br.com.microservices.orchestrated.inventoryservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.inventoryservice.core.dto.Event;
import br.com.microservices.orchestrated.inventoryservice.core.repository.OrderInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationService {
  private final OrderInventoryRepository orderInventoryRepository;

  public void checkCurrentValidation(Event event) {
    if (orderInventoryRepository.existsByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId())) {
      throw new ValidationException("Order with this transaction already exists");
    }
  }
  
  public void checkInventory(int available, int quantity) {
    if (quantity > available) {
      throw new ValidationException("Insufficient inventory for the requested quantity: " + quantity);
    }
  }
}

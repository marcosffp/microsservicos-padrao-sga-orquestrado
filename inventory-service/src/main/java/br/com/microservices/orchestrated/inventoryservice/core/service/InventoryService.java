package br.com.microservices.orchestrated.inventoryservice.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import br.com.microservices.orchestrated.inventoryservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.inventoryservice.core.dto.Event;
import br.com.microservices.orchestrated.inventoryservice.core.dto.History;
import br.com.microservices.orchestrated.inventoryservice.core.dto.Order;
import br.com.microservices.orchestrated.inventoryservice.core.dto.OrderProducts;
import br.com.microservices.orchestrated.inventoryservice.core.enums.ESagaStatus;
import br.com.microservices.orchestrated.inventoryservice.core.model.Inventory;
import br.com.microservices.orchestrated.inventoryservice.core.model.OrderInventory;
import br.com.microservices.orchestrated.inventoryservice.core.producer.KafkaProducer;
import br.com.microservices.orchestrated.inventoryservice.core.repository.InventoryRepository;
import br.com.microservices.orchestrated.inventoryservice.core.repository.OrderInventoryRepository;
import br.com.microservices.orchestrated.inventoryservice.core.utils.JsonUtil;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {
  private static final String CURRENT_SOURCE = "INVENTORY_SERVICE";

  private final JsonUtil jsonUtil;
  private final KafkaProducer producer;
  private final InventoryRepository inventoryRepository;
  private final OrderInventoryRepository orderInventoryRepository;
  private final ValidationService validationService;

  public void updateInventory(Event event) {
    try {
      validationService.checkCurrentValidation(event);
      createOrderInventory(event);
      updateInventory(event.getPayload());
      handleSuccess(event);
    } catch (Exception e) {
      log.error("Erro ao tentar atualizar o estoque", e);
      handleFailCurrentNotExecuted(event, e.getMessage());
    }
    producer.sendEvent(jsonUtil.toJson(event));
  }

  private void handleFailCurrentNotExecuted(Event event, String message) {
    event.setStatus(ESagaStatus.ROLLBACK_PENDING);
    event.setSource(CURRENT_SOURCE);
    addHistory(event, "Falha ao atualizar o estoque: " + message);
  }

  public void rollbackInventory(Event event) {
    event.setStatus(ESagaStatus.FAIL);
    event.setSource(CURRENT_SOURCE);
    try {
      returnInventoryToPreviousValues(event);
      addHistory(event, "Rollback executado para o estoque!");
    } catch (Exception e) {
      log.error("Rollback não executado para o estoque: " + e.getMessage());
    }
    producer.sendEvent(jsonUtil.toJson(event));
  }

  private void returnInventoryToPreviousValues(Event event) {
    orderInventoryRepository
        .findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
        .forEach(orderInventory -> {
          Inventory inventory = orderInventory.getInventory();
          inventory.setAvailable(orderInventory.getOldQuantity());
          inventoryRepository.save(inventory);
          log.info("Estoque restaurado para pedido {}: de {} para {}",
              event.getPayload().getId(), orderInventory.getNewQuantity(), inventory.getAvailable());
        });
  }

  private void updateInventory(Order payload) {
    payload.getProducts().forEach(p -> {
      Inventory inventory = findInventoryByProductCode(p.getProduct().getCode());
      validationService.checkInventory(inventory.getAvailable(), p.getQuantity());
      inventory.setAvailable(inventory.getAvailable() - p.getQuantity());
      inventoryRepository.save(inventory);
    });
  }

  private void createOrderInventory(Event event) {
    event.getPayload().getProducts().forEach(p -> {
      Inventory inventory = findInventoryByProductCode(p.getProduct().getCode());
      OrderInventory orderInventory = createOrderInventory(event, p, inventory);
      orderInventoryRepository.save(orderInventory);

    });
  }

  private OrderInventory createOrderInventory(Event event, OrderProducts products, Inventory inventory) {
    return OrderInventory.builder()
        .inventory(inventory)
        .oldQuantity(inventory.getAvailable())
        .orderQuantity(products.getQuantity())
        .newQuantity(inventory.getAvailable() - products.getQuantity())
        .orderId(event.getPayload().getId())
        .transactionId(event.getPayload().getTransactionId())
        .build();
  }

  private Inventory findInventoryByProductCode(String productCode) {
    return inventoryRepository.findByProductCode(productCode)
        .orElseThrow(() -> new ValidationException("Product not found: " + productCode));
  }

  private void handleSuccess(Event event) {
    event.setStatus(ESagaStatus.SUCCESS);
    event.setSource(CURRENT_SOURCE);
    addHistory(event, "Estoque realizado com sucesso!");
  }

  private void addHistory(Event event, String message) {
    History hisotry = History.builder()
        .source(event.getSource())
        .status(event.getStatus())
        .message(message)
        .createdAt(LocalDateTime.now())
        .build();
    event.addHistory(hisotry);
  }
}

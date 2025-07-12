package br.com.microservices.orchestrated.inventoryservice.core.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.microservices.orchestrated.inventoryservice.core.service.InventoryService;
import br.com.microservices.orchestrated.inventoryservice.core.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryConsumer {
  private final JsonUtil jsonUtil;
  private final InventoryService inventoryService;

  @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.inventory-success}")
  public void consumeSuccessEvent(String payload) {
    log.info("Recebido evento de inventory-success: {}", payload);
    var event = jsonUtil.toEvent(payload);
    inventoryService.updateInventory(event);
  }

  @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.inventory-fail}")
  public void consumeFailEvent(String payload) {
    log.info("Recebido evento de inventory-fail: {}", payload);
    var event = jsonUtil.toEvent(payload);
    inventoryService.rollbackInventory(event);
  }

}

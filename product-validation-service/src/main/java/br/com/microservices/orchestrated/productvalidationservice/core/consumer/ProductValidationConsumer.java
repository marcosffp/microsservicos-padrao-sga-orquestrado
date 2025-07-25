package br.com.microservices.orchestrated.productvalidationservice.core.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.microservices.orchestrated.productvalidationservice.core.service.ProductValidationService;
import br.com.microservices.orchestrated.productvalidationservice.core.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductValidationConsumer {
  private final ProductValidationService productValidationService;
  private final JsonUtil jsonUtil;

  @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", 
                 topics = "${spring.kafka.topic.product-validation-success}")
  public void consumeSuccessEvent(String payload) {
      log.info("Recebido evento de product-validation-success: {}", payload);
      var event = jsonUtil.toEvent(payload);
      productValidationService.validateExistingProducts(event);
  }

  @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", 
                 topics = "${spring.kafka.topic.product-validation-fail}")
  public void consumeFailEvent(String payload) {
    log.info("Recebido evento de product-validation-fail: {}", payload);
    var event = jsonUtil.toEvent(payload);
    productValidationService.rollbackEvent(event);
  }




}

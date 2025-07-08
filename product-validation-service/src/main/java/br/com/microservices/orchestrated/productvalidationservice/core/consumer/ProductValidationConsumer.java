package br.com.microservices.orchestrated.productvalidationservice.core.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.microservices.orchestrated.productvalidationservice.core.dto.Event;
import br.com.microservices.orchestrated.productvalidationservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ProductValidationConsumer {
  private final JsonUtil jsonUtil;

  @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.product-validation-success}")
  public void consumeSuccessEvent(String payload) {
    try {
      log.info("Receiving success event {} from product product validation success topic", payload);
      Event event = jsonUtil.toEvent(payload);
      log.info("Evento desserializado com sucesso: {}", event.getId());
      processEvent(event);
    } catch (Exception e) {
      log.error("Falha ao processar evento. Payload: {}", shortenPayload(payload), e);
    }
  }

  @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.product-validation-fail}")
  public void consumeFailEvent(String payload) {
    try {
      log.info("Receiving fail event {} from product validation fail topic", payload);
      Event event = jsonUtil.toEvent(payload);
      log.info("Evento desserializado com sucesso: {}", event.getId());
      processEvent(event);
    } catch (Exception e) {
      log.error("Falha ao processar evento. Payload: {}", shortenPayload(payload), e);
    }
  }

  private String shortenPayload(String payload) {
    return payload.length() > 200 ? payload.substring(0, 200) + "..." : payload;
  }

  private void processEvent(Event event) {
    log.info("Processando evento ID: {}", event.getId());
  }
}

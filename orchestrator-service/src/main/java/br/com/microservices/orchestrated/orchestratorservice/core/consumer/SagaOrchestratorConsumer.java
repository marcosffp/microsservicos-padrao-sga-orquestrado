package br.com.microservices.orchestrated.orchestratorservice.core.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.microservices.orchestrated.orchestratorservice.core.dto.Event;
import br.com.microservices.orchestrated.orchestratorservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class SagaOrchestratorConsumer {
  private final JsonUtil jsonUtil;

  @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.start-saga}")
  public void consumeStartSagaEvent(String payload) {
    try {
      log.info("Receiving success event {} from product start-saga", payload);
      Event event = jsonUtil.toEvent(payload);
      log.info("Evento desserializado com sucesso: {}", event.getId());
      processEvent(event);
    } catch (Exception e) {
      log.error("Falha ao processar evento. Payload: {}", shortenPayload(payload), e);
    }
  }

  @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.orchestrator}")
  public void consumeOrchestratorEvent(String payload) {
    try {
      log.info("Receiving success event {} from product orchestrator", payload);
      Event event = jsonUtil.toEvent(payload);
      log.info("Evento desserializado com sucesso: {}", event.getId());
      processEvent(event);
    } catch (Exception e) {
      log.error("Falha ao processar evento. Payload: {}", shortenPayload(payload), e);
    }
  }

  @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.finish-success}")
  public void consumeFinishSuccessEvent(String payload) {
    try {
      log.info("Receiving success event {} from product finish-success", payload);
      Event event = jsonUtil.toEvent(payload);
      log.info("Evento desserializado com sucesso: {}", event.getId());
      processEvent(event);
    } catch (Exception e) {
      log.error("Falha ao processar evento. Payload: {}", shortenPayload(payload), e);
    }
  }

  @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.finish-fail}")
  public void consumeFinishFailEvent(String payload) {
  try {
    log.info("Receiving success event {} from product finish-fail", payload);
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





package br.com.microservices.orchestrated.orchestratorservice.core.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.microservices.orchestrated.orchestratorservice.core.service.OrchestratorService;
import br.com.microservices.orchestrated.orchestratorservice.core.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaOrchestratorConsumer {
  private final JsonUtil jsonUtil;
  private final OrchestratorService orchestratorService;

  @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.start-saga}")
  public void consumeStartSagaEvent(String payload) {
    log.info("Recebendo evento de início de saga {} do topico start-saga", payload);
    var event = jsonUtil.toEvent(payload);
    orchestratorService.startSaga(event);
  }

  @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.orchestrator}")
  public void consumeOrchestratorEvent(String payload) {
    log.info("Recebendo evento de orquestrador {} do topico orquestrador", payload);
    var event = jsonUtil.toEvent(payload);
    orchestratorService.continueSaga(event);
  }

  @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.finish-success}")
  public void consumeFinishSuccessEvent(String payload) {
    log.info("Recebendo evento de orquestrador {} do topico finish-success", payload);
    var event = jsonUtil.toEvent(payload);
    orchestratorService.finishSagaSuccess(event);
  }

  @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.finish-fail}")
  public void consumeFinishFailEvent(String payload) {
    log.info("Recebendo evento de orquestrador {} do topico finish-fail", payload);
    var event = jsonUtil.toEvent(payload);
    orchestratorService.finishSagaFail(event);
  }

}

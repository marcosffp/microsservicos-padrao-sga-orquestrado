package br.com.microservices.orchestrated.orchestratorservice.core.service;

import static br.com.microservices.orchestrated.orchestratorservice.core.enums.EEventSource.ORCHESTRATOR;
import static br.com.microservices.orchestrated.orchestratorservice.core.enums.ESagaStatus.FAIL;
import static br.com.microservices.orchestrated.orchestratorservice.core.enums.ESagaStatus.SUCCESS;
import static br.com.microservices.orchestrated.orchestratorservice.core.enums.ETopics.NOTIFY_ENDING;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import br.com.microservices.orchestrated.orchestratorservice.core.dto.Event;
import br.com.microservices.orchestrated.orchestratorservice.core.dto.History;
import br.com.microservices.orchestrated.orchestratorservice.core.enums.ETopics;
import br.com.microservices.orchestrated.orchestratorservice.core.producer.SagaOrchestratorProducer;
import br.com.microservices.orchestrated.orchestratorservice.core.saga.SagaExecutionController;
import br.com.microservices.orchestrated.orchestratorservice.core.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrchestratorService {

  private final JsonUtil jsonUtil;
  private final SagaOrchestratorProducer producer;
  private final SagaExecutionController sagaExecutionController;

  public void startSaga(Event event) {
    event.setSource(ORCHESTRATOR);
    event.setStatus(SUCCESS);
    ETopics topic = getTopic(event);
    log.info("SAGA INICIADA COM SUCESSO!");
    addHistory(event, "Saga iniciada com sucesso!");
    sendToProducerWithTopic(event, topic);
  }

  public void finishSagaSuccess(Event event) {
    event.setSource(ORCHESTRATOR);
    event.setStatus(SUCCESS);
    log.info("SAGA FINALIZADA COM SUCESSO PARA O EVENTO {}", event.getId());
    addHistory(event, "Saga finalizada com sucesso!");
    notifyFinishedSaga(event);
  }

  public void finishSagaFail(Event event) {
    event.setSource(ORCHESTRATOR);
    event.setStatus(FAIL);
    log.info("SAGA FINALIZADA COM ERROS PARA O EVENTO {}", event.getId());
    addHistory(event, "Saga finalizada com erros!");
    notifyFinishedSaga(event);
  }

  public void continueSaga(Event event) {
    ETopics topic = getTopic(event);
    log.info("SAGA CONTINUANDO PARA O EVENTO {}", event.getId());
    sendToProducerWithTopic(event, topic);
  }

  private ETopics getTopic(Event event) {
    return sagaExecutionController.getNextTopic(event);
  }

  private void addHistory(Event event, String message) {
    History history = History
        .builder()
        .source(event.getSource())
        .status(event.getStatus())
        .message(message)
        .createdAt(LocalDateTime.now())
        .build();
    event.addToHistory(history);
  }

  private void notifyFinishedSaga(Event event) {
    producer.sendEvent(jsonUtil.toJson(event), NOTIFY_ENDING.getTopic());
  }

  private void sendToProducerWithTopic(Event event, ETopics topic) {
    producer.sendEvent(jsonUtil.toJson(event), topic.getTopic());
  }

}

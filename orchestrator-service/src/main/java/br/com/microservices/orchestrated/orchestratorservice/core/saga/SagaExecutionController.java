package br.com.microservices.orchestrated.orchestratorservice.core.saga;

import java.util.Arrays;

import org.springframework.stereotype.Component;

import br.com.microservices.orchestrated.orchestratorservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.orchestratorservice.core.dto.Event;
import br.com.microservices.orchestrated.orchestratorservice.core.enums.EEventSource;
import br.com.microservices.orchestrated.orchestratorservice.core.enums.ETopics;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SagaExecutionController {

  public ETopics getNextTopic(Event event) {
    if (event.getStatus() == null || event.getSource() == null) {
      throw new ValidationException("Fonte e status devem ser informados");
    }
    ETopics topic = findTopicsBySourceAndStatus(event);
    logCurrentSaga(event, topic);
    return topic;
  }

  private ETopics findTopicsBySourceAndStatus(Event event) {
    return (ETopics) Arrays.stream(SagaHandler.SAGA_HANDLER)
        .filter(row -> isEventSourceAndStatusValid(event, row))
        .map(i -> i[SagaHandler.TOPIC_INDEX])
        .findFirst()
        .orElseThrow(() -> new ValidationException("Tópico não encontrado!"));
  }

  private boolean isEventSourceAndStatusValid(Event event, Object[] row) {
    Object source = row[SagaHandler.EVENT_SOURCE_INDEX];
    Object status = row[SagaHandler.SAGA_STATUS_INDEX];
    return event.getSource().equals(source) && event.getStatus().equals(status);
  }

  private void logCurrentSaga(Event event, ETopics topic) {
    String sagaId = createSagaId(event);
    EEventSource source = event.getSource();
    switch (event.getStatus()) {
      case SUCCESS -> log.info("### SAGA ATUAL: {} | SUCESSO | PRÓXIMO TÓPICO {} | {}", source, topic, sagaId);
      case FAIL -> log.info("### SAGA ATUAL: {} | FALHA | PRÓXIMO TÓPICO {} | {}", source, topic, sagaId);
      case ROLLBACK_PENDING -> log.info(
          "### SAGA ATUAL: {} | ENVIANDO PARA ROLLBACK SERVIÇO ATUAL | PRÓXIMO TÓPICO {} | {}", source, topic, sagaId);
    }
  }

  private String createSagaId(Event event) {
    return String.format("ID PEDIDO: %s | ID TRANSAÇÃO: %s | ID EVENTO: %s",
        event.getOrderId(), event.getTransactionId(), event.getId());
  }
}

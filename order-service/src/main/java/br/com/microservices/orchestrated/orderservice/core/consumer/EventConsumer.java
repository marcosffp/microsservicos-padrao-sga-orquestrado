package br.com.microservices.orchestrated.orderservice.core.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.microservices.orchestrated.orderservice.core.utils.JsonUtil;
import br.com.microservices.orchestrated.orderservice.core.document.Event;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class EventConsumer {
  private final JsonUtil jsonUtil;

  @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.notify-ending}")
  public void consumeNotifyEndingEvent(String payload) {
    try {
      log.info("Recebido evento de notificação final");
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
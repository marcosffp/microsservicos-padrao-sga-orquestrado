package br.com.microservices.orchestrated.orderservice.core.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.microservices.orchestrated.orderservice.core.utils.JsonUtil;
import br.com.microservices.orchestrated.orderservice.core.document.Event;
import br.com.microservices.orchestrated.orderservice.core.service.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class EventConsumer {

  private final JsonUtil jsonUtil;
  private final EventService eventService;

  @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.notify-ending}")
  public void consumeNotifyEndingEvent(String payload) {
    log.info("Recebido evento de notify-ending: {}", payload);
    Event event = jsonUtil.toEvent(payload);
    processEvent(event);
  }

  private void processEvent(Event event) {
    eventService.notifyEnding(event);
    log.info("Evento processado com sucesso: {}", event.getId());
  }
}
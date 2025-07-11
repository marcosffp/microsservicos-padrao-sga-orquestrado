
package br.com.microservices.orchestrated.orderservice.core.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.microservices.orchestrated.orderservice.core.service.EventService;
import br.com.microservices.orchestrated.orderservice.core.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventConsumer {

  private final JsonUtil jsonUtil;
  private final EventService eventService;

  @KafkaListener(topics = "${spring.kafka.topic.notify-ending}", 
                 groupId = "${spring.kafka.consumer.group-id}")
  public void consumeNotifyEndingEvent(String payload) {
    log.info("Recebido evento de notify-ending: {}", payload);

    var event = jsonUtil.toEvent(payload);
    eventService.notifyEnding(event);
    
    log.info("Evento processado com sucesso: {}", event.getId());
  }
}
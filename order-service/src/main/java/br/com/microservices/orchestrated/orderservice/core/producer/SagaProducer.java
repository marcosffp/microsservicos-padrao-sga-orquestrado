package br.com.microservices.orchestrated.orderservice.core.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;

  @Value("${spring.kafka.topic.start-saga}")
  private String startSagaTopic;


  public void sendEvent(String payload) {
    try {

      log.info("Enviando evento para o tópico {}: {}", startSagaTopic, payload);
      kafkaTemplate.send(startSagaTopic, payload);

    } catch (Exception e) {
      log.error("Erro ao enviar para o tópico {}: {}", startSagaTopic, payload, e);
    }
  }
}

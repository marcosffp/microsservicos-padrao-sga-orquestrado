package br.com.microservices.orchestrated.orchestratorservice.core.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaOrchestratorProducer {
  private final KafkaTemplate<String, String> kafkaTemplate;

  public void sendEvent(String payload, String topic) {
    try {
      log.info("Enviando evento para o tópico {} com dados {}", topic, payload);
      kafkaTemplate.send(topic, payload);
    } catch (Exception e) {
      log.error("Erro ao enviar dados para o tópico {} com dados {}. Erro: {}", topic, payload, e.getMessage());
    }
  }
}

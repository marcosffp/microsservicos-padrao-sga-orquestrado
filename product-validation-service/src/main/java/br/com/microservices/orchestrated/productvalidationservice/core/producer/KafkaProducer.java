package br.com.microservices.orchestrated.productvalidationservice.core.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Value("${spring.kafka.topic.orchestrator}")
  private String orchestratorTopic;

  public void sendEvent(String payload) {
    try {

      log.info("Enviando evento para o tópico {}: {}", orchestratorTopic, payload);
      kafkaTemplate.send(orchestratorTopic, payload);
      
    } catch (Exception e) {
      log.error("Erro ao enviar para o tópico {}: {}", orchestratorTopic, payload, e.getMessage());
    }
  }
}

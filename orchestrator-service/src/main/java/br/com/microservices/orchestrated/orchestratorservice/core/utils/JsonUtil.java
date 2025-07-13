package br.com.microservices.orchestrated.orchestratorservice.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.microservices.orchestrated.orchestratorservice.core.dto.Event;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class JsonUtil {

  private final ObjectMapper objectMapper;

  @PostConstruct
  private void configurarMapper() {
    JavaTimeModule javaTimeModule = new JavaTimeModule();
    javaTimeModule.addDeserializer(java.time.LocalDateTime.class, new LocalDateTimeDeserializer());

    objectMapper.registerModule(javaTimeModule);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  public String toJson(Object objeto) {
    try {
      return objectMapper.writeValueAsString(objeto);
    } catch (Exception e) {
      log.error("Erro ao serializar objeto para JSON", e);
      throw new RuntimeException("Falha ao serializar objeto para JSON", e);
    }
  }

  public Event toEvent(String json) {
    try {
      return objectMapper.readValue(json, Event.class);
    } catch (Exception e) {
      log.error("Erro ao desserializar JSON para Event. JSON: {}", json, e);
      throw new RuntimeException("Falha ao desserializar evento", e);
    }
  }
}

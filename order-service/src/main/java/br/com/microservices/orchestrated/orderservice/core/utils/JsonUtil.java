package br.com.microservices.orchestrated.orderservice.core.utils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.core.JsonParser;

import br.com.microservices.orchestrated.orderservice.core.document.Event;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class JsonUtil {
  private final ObjectMapper objectMapper;
  

    @PostConstruct
    public void configureMapper() {
        JavaTimeModule module = new JavaTimeModule();
        
        // Configura um desserializador customizado para LocalDateTime
        module.addDeserializer(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) 
                throws IOException {
                String value = p.getText();
                // Remove os nanossegundos extras mantendo apenas milissegundos
                if (value.length() > 23) {
                    value = value.substring(0, 23);
                }
                return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        });
        
        objectMapper.registerModule(module);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

  public String toJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (Exception e) {
      log.error("Error serializing object to JSON", e);
      return "";
    }
  }

  public Event toEvent(String json) {
    try {
      return objectMapper.readValue(json, Event.class);
    } catch (Exception e) {
      log.error("Error deserializing JSON to Event. JSON: {}", json, e);
      throw new RuntimeException("Failed to deserialize event", e);
    }
  }
}

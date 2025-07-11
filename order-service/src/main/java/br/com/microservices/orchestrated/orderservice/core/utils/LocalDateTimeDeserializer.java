package br.com.microservices.orchestrated.orderservice.core.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

@Component
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

  @Override
  public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    String dateValue = parser.getText();
    if (dateValue.length() > 23) {
      dateValue = dateValue.substring(0, 23);
    }
    return LocalDateTime.parse(dateValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
  }
}

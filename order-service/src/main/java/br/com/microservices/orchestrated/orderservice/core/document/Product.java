package br.com.microservices.orchestrated.orderservice.core.document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
  @JsonProperty("code")
  private String code;

  @JsonProperty("unitValue")
  private double unitValue;
}

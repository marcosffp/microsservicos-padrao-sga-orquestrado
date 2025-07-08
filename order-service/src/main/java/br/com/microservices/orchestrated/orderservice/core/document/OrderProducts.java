package br.com.microservices.orchestrated.orderservice.core.document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProducts {
  @JsonProperty("product")
  private Product product;

  @JsonProperty("quantity")
  private int quantity;
}

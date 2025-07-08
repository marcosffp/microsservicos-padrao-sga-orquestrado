package br.com.microservices.orchestrated.orderservice.core.dto;

import br.com.microservices.orchestrated.orderservice.core.document.OrderProducts;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;



public record OrderRequest(
    @JsonProperty("products") List<OrderProducts> products) {
}

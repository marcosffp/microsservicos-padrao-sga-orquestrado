package br.com.microservices.orchestrated.orderservice.core.dto;

import java.util.List;
import br.com.microservices.orchestrated.orderservice.core.document.OrderProducts;

public record OrderRequest(List<OrderProducts> products) {
}

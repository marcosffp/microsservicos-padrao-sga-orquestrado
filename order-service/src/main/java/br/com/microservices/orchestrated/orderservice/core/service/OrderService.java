package br.com.microservices.orchestrated.orderservice.core.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import br.com.microservices.orchestrated.orderservice.core.document.Event;
import br.com.microservices.orchestrated.orderservice.core.document.Order;
import br.com.microservices.orchestrated.orderservice.core.dto.OrderRequest;
import br.com.microservices.orchestrated.orderservice.core.producer.SagaProducer;
import br.com.microservices.orchestrated.orderservice.core.repository.OrderRepository;
import br.com.microservices.orchestrated.orderservice.core.utils.JsonUtil;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final OrderRepository orderRepository;
  private final JsonUtil jsonUtil;
  private final EventService eventService;
  private final SagaProducer sagaProducer;

  private static final String TRANSACTION_ID_PATTERN = "%s_%s";

  public Order createOrder(OrderRequest orderRequest) {
    var products = orderRequest.products();
    Order order = Order.builder()
        .products(products)
        .createdAt(LocalDateTime.now())
        .transactionId(String.format(TRANSACTION_ID_PATTERN, Instant.now().toEpochMilli(), UUID.randomUUID()))
        .build();
    orderRepository.save(order);
    sagaProducer.sendEvent(jsonUtil.toJson(createPayload(order)));

    return order;
  }

  private Event createPayload(Order order) {
    return eventService.save(Event.builder()
        .orderId(order.getId())
        .transactionId(order.getTransactionId())
        .payload(order)
        .createdAt(LocalDateTime.now())
        .build());
  }
}

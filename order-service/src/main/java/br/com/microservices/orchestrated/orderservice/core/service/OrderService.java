package br.com.microservices.orchestrated.orderservice.core.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import br.com.microservices.orchestrated.orderservice.core.document.Event;
import br.com.microservices.orchestrated.orderservice.core.document.Order;
import br.com.microservices.orchestrated.orderservice.core.dto.OrderRequest;
import br.com.microservices.orchestrated.orderservice.core.producer.SagaProducer;
import br.com.microservices.orchestrated.orderservice.core.repository.OrderRepository;
import br.com.microservices.orchestrated.orderservice.core.utils.JsonUtil;

@Service
public class OrderService {
  @Autowired
  private OrderRepository orderRepository;

  private static final String TRANSACTION_ID_PATTERN = "%s_%s";

  @Autowired
  private JsonUtil jsonUtil;

  @Autowired
  private EventService eventService;

  @Autowired
  private SagaProducer sagaProducer;

  public Order createOrder(OrderRequest orderRequest) {
    var products = orderRequest.products();
    
    // Calcula o total de itens
    int totalItems = products.stream()
        .mapToInt(orderProduct -> orderProduct.getQuantity())
        .sum();
    
    // Calcula o valor total
    double totalAmount = products.stream()
        .mapToDouble(orderProduct -> orderProduct.getProduct().getUnitValue() * orderProduct.getQuantity())
        .sum();
    
    Order order = Order.builder()
        .products(products)
        .createdAt(LocalDateTime.now())
        .transactionId(String.format(TRANSACTION_ID_PATTERN, Instant.now().toEpochMilli(), UUID.randomUUID()))
        .totalAmount(totalAmount)
        .totalItems(totalItems)
        .build();

    orderRepository.save(order);
    sagaProducer.sendEvent(jsonUtil.toJson(createPayload(order)));

    return order;
  }

  private Event createPayload(Order order) {
    Event event = Event.builder()
        .orderId(order.getId())
        .transactionId(order.getTransactionId())
        .payload(order)
        .createdAt(LocalDateTime.now())
        .build();

    return eventService.save(event);

  }
}

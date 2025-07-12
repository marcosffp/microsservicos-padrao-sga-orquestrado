package br.com.microservices.orchestrated.paymentservice.core.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.microservices.orchestrated.paymentservice.core.service.PaymentService;
import br.com.microservices.orchestrated.paymentservice.core.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

  private final JsonUtil jsonUtil;
  private final PaymentService paymentService;

  @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.payment-success}")
  public void consumeSuccessEvent(String payload) {
    log.info("Recebido evento de paymnet-success: {}", payload);
    var event = jsonUtil.toEvent(payload);
    paymentService.realizePayment(event);
  }

  @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.payment-fail}")
  public void consumeFailEvent(String payload) {
    log.info("Recebido evento de paymnet-fail: {}", payload);
    var event = jsonUtil.toEvent(payload);
    paymentService.realizeRefund(event);
  }

}

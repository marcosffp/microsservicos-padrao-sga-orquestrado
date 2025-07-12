package br.com.microservices.orchestrated.paymentservice.core.service;

import org.apache.kafka.common.security.oauthbearer.internals.secured.ValidateException;
import org.springframework.stereotype.Component;

import br.com.microservices.orchestrated.paymentservice.core.dto.Event;
import br.com.microservices.orchestrated.paymentservice.core.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationService {
  private final PaymentRepository paymentRepository;
  private static final Double MINIMUM_AMOUNT = 0.1;

  public void checkCurrentValidation(Event event) {
    if (paymentRepository.existsByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId())) {
      throw new ValidateException("Já existe outro orderId e transactionId para esta validação");
    }
  }

  public void validateAmount(double amount) {
    if (MINIMUM_AMOUNT < 0.1) {
      throw new ValidateException("O valor deve ser maior que " + MINIMUM_AMOUNT);
    }
  }
}

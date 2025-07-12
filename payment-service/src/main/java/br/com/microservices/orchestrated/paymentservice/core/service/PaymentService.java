package br.com.microservices.orchestrated.paymentservice.core.service;

import java.time.LocalDateTime;

import org.apache.kafka.common.security.oauthbearer.internals.secured.ValidateException;
import org.springframework.stereotype.Service;

import br.com.microservices.orchestrated.paymentservice.core.dto.Event;
import br.com.microservices.orchestrated.paymentservice.core.dto.History;
import br.com.microservices.orchestrated.paymentservice.core.dto.OrderProducts;
import br.com.microservices.orchestrated.paymentservice.core.enums.EPaymentStatus;
import br.com.microservices.orchestrated.paymentservice.core.enums.ESagaStatus;
import br.com.microservices.orchestrated.paymentservice.core.model.Payment;
import br.com.microservices.orchestrated.paymentservice.core.producer.KafkaProducer;
import br.com.microservices.orchestrated.paymentservice.core.repository.PaymentRepository;
import br.com.microservices.orchestrated.paymentservice.core.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
  private static final String CURRENT_SOURCE = "PAYMENT_SERVICE";
  private static final Double REDUCE_SUM_VALUE = 0.0;
  private final PaymentRepository paymentRepository;
  private final ValidationService validationService;
  private final JsonUtil jsonUtil;
  private final KafkaProducer producer;

  public void realizePayment(Event event) {
    try {
      validationService.checkCurrentValidation(event);
      createPendingPayment(event);
      var payment = findByOrderIdAndTransactionId(event);
      validationService.validateAmount(payment.getTotalAmount());
      changePaymentToSuccess(payment);
      handleSuccess(event);
    } catch (Exception e) {
      log.error("Erro ao tentar realizar o pagamento: ", e);
      handleFailCurrentNotExecuted(event, e.getMessage());
    }
    producer.sendEvent(jsonUtil.toJson(event));
  }

  private void handleFailCurrentNotExecuted(Event event, String message) {
    event.setStatus(ESagaStatus.ROLLBACK_PENDING);
    event.setSource(CURRENT_SOURCE);
    addHistory(event, "Pagamento não realizado: " + message);
  }

  private void createPendingPayment(Event event) {
    double totalAmount = calculateAmount(event);
    int totalItems = calculateItems(event);
    var payment = Payment.builder()
        .orderId(event.getPayload().getId())
        .transaciontId(event.getTransactionId())
        .totalItems(totalItems)
        .totalAmount(totalAmount)
        .build();

    save(payment);
    setEventAmountItems(event, payment);
  }

  private double calculateAmount(Event event) {
    return event.getPayload().getProducts().stream()
        .map(p -> p.getQuantity() * p.getProduct().getUnitValue()).reduce(REDUCE_SUM_VALUE, Double::sum);
  }

  private int calculateItems(Event event) {
    return event.getPayload().getProducts().stream()
        .map(OrderProducts::getQuantity).reduce(0, Integer::sum);
  }

  private void save(Payment payment) {
    paymentRepository.save(payment);
  }

  private void setEventAmountItems(Event event, Payment payment) {
    event.getPayload().setTotalAmount(payment.getTotalAmount());
    event.getPayload().setTotalItems(payment.getTotalItems());
  }

  private Payment findByOrderIdAndTransactionId(Event event) {
    return paymentRepository.findByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId())
        .orElseThrow(() -> new ValidateException("Pagamento não encontrado para orderId: " + event.getOrderId()
            + " e transactionId: " + event.getTransactionId()));
  }

  private void changePaymentToSuccess(Payment payment) {
    payment.setStatus(EPaymentStatus.SUCCESS);
    save(payment);
  }

  private void handleSuccess(Event event) {
    event.setStatus(ESagaStatus.SUCCESS);
    event.setSource(CURRENT_SOURCE);
    addHistory(event, "Pagamento realizado com sucesso!");
  }

  private void addHistory(Event event, String message) {
    var hisotry = History.builder()
        .source(event.getSource())
        .status(event.getStatus())
        .message(message)
        .createdAt(LocalDateTime.now())
        .build();
    event.addHistory(hisotry);
  }

  public void realizeRefund(Event event) {
    event.setStatus(ESagaStatus.FAIL);
    event.setSource(CURRENT_SOURCE);
    try {
      changePaymentStatusToRefund(event);
      addHistory(event, "Estorno realizado para o pagamento!");
    } catch (Exception e) {
      addHistory(event, "Estorno não realizado para o pagamento: " + e.getMessage());
    }
    producer.sendEvent(jsonUtil.toJson(event));
  }

  private void changePaymentStatusToRefund(Event event) {
    var payment = findByOrderIdAndTransactionId(event);
    payment.setStatus(EPaymentStatus.REFUND);
    setEventAmountItems(event, payment);
    save(payment);
  }

}

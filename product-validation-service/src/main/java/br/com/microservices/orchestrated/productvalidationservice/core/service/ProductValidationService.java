package br.com.microservices.orchestrated.productvalidationservice.core.service;

import br.com.microservices.orchestrated.productvalidationservice.core.dto.Event;
import br.com.microservices.orchestrated.productvalidationservice.core.dto.History;
import br.com.microservices.orchestrated.productvalidationservice.core.model.Validation;
import br.com.microservices.orchestrated.productvalidationservice.core.producer.KafkaProducer;
import br.com.microservices.orchestrated.productvalidationservice.core.repository.ValidationRepository;
import br.com.microservices.orchestrated.productvalidationservice.core.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.microservices.orchestrated.productvalidationservice.core.enums.ESagaStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductValidationService {

  private static final String CURRENT_SOURCE = "PRODUCT_VALIDATION_SERVICE";

  private final JsonUtil jsonUtil;
  private final KafkaProducer producer;
  private final ValidationRepository validationRepository;
  private final ValidationService validationService;

  public void validateExistingProducts(Event event) {
    try {
      validationService.checkCurrentValidation(event);
      createValidation(event, true);
      handleSuccess(event);
    } catch (Exception ex) {
      log.error("Erro ao tentar validar produto: ", ex);
      handleFailCurrentNotExecuted(event, ex.getMessage());
    }
    producer.sendEvent(jsonUtil.toJson(event));
  }

  private void createValidation(Event event, boolean success) {
    var validation = Validation
        .builder()
        .orderId(event.getPayload().getId())
        .transactionId(event.getTransactionId())
        .success(success)
        .build();
    validationRepository.save(validation);
  }

  private void handleSuccess(Event event) {
    event.setStatus(SUCCESS);
    event.setSource(CURRENT_SOURCE);
    addHistory(event, "Validação dos produtos realizada com sucesso.");
  }

  private void addHistory(Event event, String message) {
    var history = History
        .builder()
        .source(event.getSource())
        .status(event.getStatus())
        .message(message)
        .createdAt(LocalDateTime.now())
        .build();
    event.addHistory(history);
  }

  private void handleFailCurrentNotExecuted(Event event, String message) {
    event.setStatus(ROLLBACK_PENDING);
    event.setSource(CURRENT_SOURCE);
    addHistory(event, "Erro na validação dos produtos: " + message);
  }

  public void rollbackEvent(Event event) {
    changeValidationToFail(event);
    event.setStatus(FAIL);
    event.setSource(CURRENT_SOURCE);
    addHistory(event, "Rollback realizado na validação dos produtos.");
    producer.sendEvent(jsonUtil.toJson(event));
  }

  private void changeValidationToFail(Event event) {
    validationRepository
        .findByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId())
        .ifPresentOrElse(validation -> {
          validation.setSuccess(false);
          validationRepository.save(validation);
        },
            () -> createValidation(event, false));
  }
}
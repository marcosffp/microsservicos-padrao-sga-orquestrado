package br.com.microservices.orchestrated.productvalidationservice.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.microservices.orchestrated.productvalidationservice.core.dto.Event;
import br.com.microservices.orchestrated.productvalidationservice.core.producer.KafkaProducer;
import br.com.microservices.orchestrated.productvalidationservice.core.repository.ProductReposiroty;
import br.com.microservices.orchestrated.productvalidationservice.core.repository.ValidationRepository;
import br.com.microservices.orchestrated.productvalidationservice.core.utils.JsonUtil;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductValidationService {
  private static final String CURRENT_SOURCE = "PRODUCT_VALIDATION_SERVICE";
  @Autowired
  private JsonUtil jsonUtil;
  @Autowired
  private KafkaProducer kafkaProducer;
  @Autowired
  private ProductReposiroty productReposiroty;
  @Autowired
  private ValidationRepository validationRepository;


  public void validateExistingProducts(Event event) {
    try {
      checkIfEventIsValid(event);
      createValidation(event);
      handleSuccess(event);
      log.info("Validating products for event {}", event.getId());
    } catch (Exception e) {
      log.error("Error validating products for event {}: {}", event.getId(), e.getMessage());
      handleFailCurrentNotExecuted(event, e.getMessage());
    }
    kafkaProducer.sendEvent(jsonUtil.toJson(event));
  }


  private void createValidation(Event event) {
    validateExistingProducts(event);
    if (validationRepository.existsByOrderIdAndTransactionId(event.getPayload().getId(),
        event.getPayload().getTransactionId())) {
      throw new ValidationException("Validation already exists for this order and transaction");
    }
    event.getPayload().getProducts().forEach(p-> {
      if (p.getProduct()==null|| p.getProduct().getCode() == null) {
        throw new ValidationException("Product code is null");  
      }
    });
  }
  
  private void validateProductsInformed(Event event) {
    if (event.getPayload() == null || event.getPayload().getProducts() == null) {
      throw new ValidationException("Product list is null or empty");
    }

    if (event.getPayload().getId() == null || event.getPayload().getTransactionId().isEmpty()) {
      throw new ValidationException("Order ID or Transaction ID is null or empty");

    }
  }

}

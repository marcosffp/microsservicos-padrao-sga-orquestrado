package br.com.microservices.orchestrated.productvalidationservice.core.service;

import org.springframework.stereotype.Component;

import br.com.microservices.orchestrated.productvalidationservice.core.dto.Event;
import br.com.microservices.orchestrated.productvalidationservice.core.dto.OrderProducts;
import br.com.microservices.orchestrated.productvalidationservice.core.repository.ProductReposiroty;
import br.com.microservices.orchestrated.productvalidationservice.core.repository.ValidationRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ValidationService {
  private final ValidationRepository validationRepository;
  private final ProductReposiroty productRepository;

  public void checkCurrentValidation(Event event) {
    var payload = event.getPayload();
    var transactionId = event.getTransactionId();
    validateProductsInformed(payload, transactionId);

    String orderId = payload.getId();
    if (validationRepository.existsByOrderIdAndTransactionId(orderId, transactionId)) {
      throw new ValidationException("Já existe uma validação para este pedido e transação.");
    }

    payload.getProducts().forEach(this::validateAndCheckProduct);
  }

  private void validateProductsInformed(
      br.com.microservices.orchestrated.productvalidationservice.core.dto.Order payload, String transactionId) {
    if (payload == null || payload.getProducts() == null || payload.getProducts().isEmpty()) {
      throw new ValidationException("A lista de produtos está vazia!");
    }
    if (payload.getId() == null || transactionId == null) {
      throw new ValidationException("OrderID e TransactionID devem ser informados!");
    }
  }

  private void validateAndCheckProduct(OrderProducts product) {
    if (product.getProduct() == null || product.getProduct().getCode() == null
        || product.getProduct().getCode().isEmpty()) {
      throw new ValidationException("Produto deve ser informado!");
    }
    if (!productRepository.existsByCode(product.getProduct().getCode())) {
      throw new ValidationException("Produto não existe na base de dados!");
    }
  }
}

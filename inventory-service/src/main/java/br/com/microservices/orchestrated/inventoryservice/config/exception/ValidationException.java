package br.com.microservices.orchestrated.inventoryservice.config.exception;


public class ValidationException extends RuntimeException {
  public ValidationException(String message) {
    super(message);
  }
}

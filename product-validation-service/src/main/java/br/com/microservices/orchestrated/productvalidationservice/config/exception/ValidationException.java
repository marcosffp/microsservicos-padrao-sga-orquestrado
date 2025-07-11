package br.com.microservices.orchestrated.productvalidationservice.config.exception;


public class ValidationException extends RuntimeException {
  public ValidationException(String message) {
    super(message);
  }
}

package br.com.microservices.orchestrated.orderservice.config.exception;


public class ValidationException extends RuntimeException {
  public ValidationException(String message) {
    super(message);
  }
}

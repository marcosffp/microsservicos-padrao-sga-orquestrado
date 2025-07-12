package br.com.microservices.orchestrated.paymentservice.config.exception;


public class ValidationException extends RuntimeException {
  public ValidationException(String message) {
    super(message);
  }
}

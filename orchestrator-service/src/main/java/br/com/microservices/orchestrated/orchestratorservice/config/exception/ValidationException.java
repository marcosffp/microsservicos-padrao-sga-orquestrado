package br.com.microservices.orchestrated.orchestratorservice.config.exception;


public class ValidationException extends RuntimeException {
  public ValidationException(String message) {
    super(message);
  }
}

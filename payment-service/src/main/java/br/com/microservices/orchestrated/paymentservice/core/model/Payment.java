package br.com.microservices.orchestrated.paymentservice.core.model;

import java.time.LocalDateTime;

import br.com.microservices.orchestrated.paymentservice.core.enums.EPaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment")
public class Payment {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private String orderId;

  @Column(nullable = false)
  private String transaciontId;

  @Column(nullable = false,updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @Column(nullable = false)
  private int totalItems;

  @Column(nullable = false)
  private double totalAmount;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private EPaymentStatus status;

  @PrePersist
  public void prePersist() {
    this.updatedAt = this.createdAt = LocalDateTime.now();
    this.status = EPaymentStatus.PENDING;
  }
  
  @PreUpdate
  public void preUpdate() {
    this.updatedAt = LocalDateTime.now();
  }


}

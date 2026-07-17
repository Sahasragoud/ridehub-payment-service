package com.ridehub.paymentservice.entity;

import com.ridehub.paymentservice.enums.Currency;
import com.ridehub.paymentservice.enums.PaymentMethod;
import com.ridehub.paymentservice.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long rideId;

    @Column(nullable = false)
    private Long payerId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(unique = true)
    private String transactionId;

    private String gateway;

    private String gatewayOrderId;

    private String gatewayPaymentId;

    private String failureReason;

    @Column(unique = true)
    private String receiptNumber;

    private String gatewayResponseCode;

    private String gatewayMessage;

    private LocalDateTime processedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
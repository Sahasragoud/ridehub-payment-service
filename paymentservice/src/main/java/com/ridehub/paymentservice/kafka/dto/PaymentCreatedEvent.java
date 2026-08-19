package com.ridehub.paymentservice.kafka.dto;

import com.ridehub.paymentservice.enums.Currency;
import com.ridehub.paymentservice.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentCreatedEvent {

    private Long paymentId;

    private Long rideId;

    private Long payerId;

    private BigDecimal amount;

    private Currency currency;

    private String transactionId;

    private LocalDateTime createdAt;

    private PaymentStatus status;

}
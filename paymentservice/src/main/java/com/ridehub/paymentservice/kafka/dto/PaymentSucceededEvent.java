package com.ridehub.paymentservice.kafka.dto;

import com.ridehub.paymentservice.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentSucceededEvent {

    private Long paymentId;

    private Long rideId;

    private BigDecimal amount;

    private String transactionId;

    private String receiptNumber;

    private LocalDateTime processedAt;

    private PaymentStatus status;

}
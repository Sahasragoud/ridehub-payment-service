package com.ridehub.paymentservice.kafka.dto;

import com.ridehub.paymentservice.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentRefundedEvent {

    private Long refundId;

    private Long paymentId;

    private Long rideId;

    private BigDecimal amount;

    private String refundTransactionId;

    private LocalDateTime processedAt;

    private PaymentStatus status;

}
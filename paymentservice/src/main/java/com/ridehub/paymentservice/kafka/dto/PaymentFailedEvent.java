package com.ridehub.paymentservice.kafka.dto;

import com.ridehub.paymentservice.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PaymentFailedEvent {

    private Long paymentId;

    private Long rideId;

    private String transactionId;

    private String reason;

    private LocalDateTime failedAt;

    private PaymentStatus status;

}
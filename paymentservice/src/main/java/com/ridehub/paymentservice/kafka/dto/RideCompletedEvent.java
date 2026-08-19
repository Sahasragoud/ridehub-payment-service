package com.ridehub.paymentservice.kafka.dto;

import com.ridehub.paymentservice.enums.Currency;
import com.ridehub.paymentservice.enums.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideCompletedEvent {

    private Long rideId;

    private Long riderId;

    private Long driverId;

    private BigDecimal fare;

    private LocalDateTime completedAt;

    private Currency currency;

    private PaymentMethod method;
}

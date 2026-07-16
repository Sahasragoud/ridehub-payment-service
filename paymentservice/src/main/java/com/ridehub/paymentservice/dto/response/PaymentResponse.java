package com.ridehub.paymentservice.dto.response;

import com.ridehub.paymentservice.enums.Currency;
import com.ridehub.paymentservice.enums.PaymentMethod;
import com.ridehub.paymentservice.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponse {

    private Long id;

    private Long rideId;

    private Long payerId;

    private BigDecimal amount;

    private Currency currency;

    private PaymentMethod paymentMethod;

    private PaymentStatus status;

    private String transactionId;

    private String gateway;

    private LocalDateTime createdAt;

}
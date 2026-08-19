package com.ridehub.paymentservice.dto.request;

import com.ridehub.paymentservice.enums.Currency;
import com.ridehub.paymentservice.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class PaymentRequest {

    @NotNull
    private Long rideId;

    @NotNull
    private Long payerId;

    @NotNull
    @DecimalMin("1.00")
    private BigDecimal amount;

    @NotNull
    private Currency currency;

    @NotNull
    private PaymentMethod paymentMethod;

}
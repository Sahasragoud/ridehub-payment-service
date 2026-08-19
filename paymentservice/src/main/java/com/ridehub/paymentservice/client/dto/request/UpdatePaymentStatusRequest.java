package com.ridehub.paymentservice.client.dto.request;

import com.ridehub.paymentservice.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdatePaymentStatusRequest {

    private PaymentStatus paymentStatus;

}
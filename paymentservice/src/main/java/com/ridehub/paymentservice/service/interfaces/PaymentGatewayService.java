package com.ridehub.paymentservice.service.interfaces;

import com.ridehub.paymentservice.entity.Payment;

public interface PaymentGatewayService {

    Payment processPayment(Payment payment);

}
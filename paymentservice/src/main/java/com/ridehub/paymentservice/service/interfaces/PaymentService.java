package com.ridehub.paymentservice.service.interfaces;

import com.ridehub.paymentservice.dto.request.PaymentRequest;
import com.ridehub.paymentservice.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse createPayment(
            PaymentRequest request,
            String idempotencyKey);


    PaymentResponse getPayment(Long paymentId);

    PaymentResponse getPaymentByRide(Long rideId);

    List<PaymentResponse> getPaymentHistory(Long payerId);

}
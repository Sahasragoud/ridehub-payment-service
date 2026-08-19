package com.ridehub.paymentservice.client.controller;

import com.ridehub.paymentservice.dto.request.PaymentRequest;
import com.ridehub.paymentservice.dto.response.PaymentResponse;
import com.ridehub.paymentservice.service.interfaces.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/payments")
@RequiredArgsConstructor
public class InternalPaymentController {

    private final PaymentService paymentService;

    @PostMapping
    PaymentResponse createPayment(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody PaymentRequest request
    ){
        return paymentService.createPayment(request, idempotencyKey);
    }
}
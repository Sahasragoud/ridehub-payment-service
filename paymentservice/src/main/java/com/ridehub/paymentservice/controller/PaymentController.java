package com.ridehub.paymentservice.controller;

import com.ridehub.paymentservice.dto.request.PaymentRequest;
import com.ridehub.paymentservice.dto.response.PaymentResponse;
import com.ridehub.paymentservice.service.interfaces.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PaymentResponse createPayment(
            @RequestHeader(name = "Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody PaymentRequest request) {

        return paymentService.createPayment(
                request,
                idempotencyKey
        );
    }

    @GetMapping("/{paymentId}")
    public PaymentResponse getPayment(
            @PathVariable Long paymentId) {

        return paymentService.getPayment(paymentId);
    }

    @GetMapping("/ride/{rideId}")
    public PaymentResponse getPaymentByRide(
            @PathVariable Long rideId) {

        return paymentService.getPaymentByRide(rideId);
    }

    @GetMapping("/history/{payerId}")
    public List<PaymentResponse> getPaymentHistory(
            @PathVariable Long payerId) {

        return paymentService.getPaymentHistory(payerId);
    }



}
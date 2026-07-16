package com.ridehub.paymentservice.controller;

import com.ridehub.paymentservice.dto.request.PaymentRequest;
import com.ridehub.paymentservice.dto.response.PaymentResponse;
import com.ridehub.paymentservice.service.interfaces.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse createPayment(
            @Valid @RequestBody PaymentRequest request) {

        return paymentService.createPayment(request);
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
package com.ridehub.paymentservice.service.impl;

import com.ridehub.paymentservice.dto.request.PaymentRequest;
import com.ridehub.paymentservice.dto.response.PaymentResponse;
import com.ridehub.paymentservice.entity.Payment;
import com.ridehub.paymentservice.enums.PaymentStatus;
import com.ridehub.paymentservice.exception.ResourceNotFoundException;
import com.ridehub.paymentservice.repository.PaymentRepository;
import com.ridehub.paymentservice.service.interfaces.PaymentGatewayService;
import com.ridehub.paymentservice.service.interfaces.PaymentService;
import com.ridehub.paymentservice.util.TransactionIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentGatewayService paymentGatewayService;

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {

        log.info("Creating payment for ride {}", request.getRideId());

        Payment payment = Payment.builder()
                .rideId(request.getRideId())
                .payerId(request.getPayerId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .transactionId(TransactionIdGenerator.generate())
                .gateway("MOCK_GATEWAY")
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        savedPayment = paymentGatewayService.processPayment(savedPayment);

        return mapToResponse(savedPayment);

    }

    @Override
    public PaymentResponse getPayment(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found."));

        return mapToResponse(payment);

    }

    @Override
    public PaymentResponse getPaymentByRide(Long rideId) {

        Payment payment = paymentRepository.findByRideId(rideId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found."));

        return mapToResponse(payment);

    }

    @Override
    public List<PaymentResponse> getPaymentHistory(Long payerId) {

        return paymentRepository.findAllByPayerId(payerId)
                .stream()
                .map(this::mapToResponse)
                .toList();

    }

    private PaymentResponse mapToResponse(Payment payment) {

        return PaymentResponse.builder()
                .id(payment.getId())
                .rideId(payment.getRideId())
                .payerId(payment.getPayerId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .gateway(payment.getGateway())
                .createdAt(payment.getCreatedAt())
                .build();

    }
}
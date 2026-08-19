package com.ridehub.paymentservice.service.impl;

import com.ridehub.paymentservice.audit.enums.AuditEvent;
import com.ridehub.paymentservice.audit.service.interfaces.AuditService;
import com.ridehub.paymentservice.dto.request.PaymentRequest;
import com.ridehub.paymentservice.dto.response.PaymentResponse;
import com.ridehub.paymentservice.entity.Payment;
import com.ridehub.paymentservice.enums.PaymentStatus;
import com.ridehub.paymentservice.exception.GatewayTimeoutException;
import com.ridehub.paymentservice.exception.ResourceNotFoundException;
import com.ridehub.paymentservice.kafka.dto.PaymentFailedEvent;
import com.ridehub.paymentservice.kafka.dto.PaymentTimeoutEvent;
import com.ridehub.paymentservice.kafka.publisher.PaymentEventPublisher;
import com.ridehub.paymentservice.repository.PaymentRepository;
import com.ridehub.paymentservice.service.interfaces.PaymentGatewayService;
import com.ridehub.paymentservice.service.interfaces.PaymentService;
import com.ridehub.paymentservice.util.ReceiptGenerator;
import com.ridehub.paymentservice.util.TransactionIdGenerator;
import com.ridehub.paymentservice.kafka.dto.PaymentSucceededEvent;
import com.ridehub.paymentservice.kafka.dto.PaymentCreatedEvent;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentGatewayService paymentGatewayService;
    private final TransactionIdGenerator transactionIdGenerator;
    private final ReceiptGenerator receiptGenerator;
    private final AuditService auditService;
    private final PaymentEventPublisher paymentEventPublisher;

    @Value("${payment.retry.max-attempts}")
    private int maxRetryAttempts;

    @Value("${payment.retry.delay}")
    private long retryDelay;

    @Value("${payment.timeout}")
    private long paymentTimeout;

    @Override
    public PaymentResponse createPayment(
            PaymentRequest request,
            String idempotencyKey) {

        log.info("Creating payment for ride {}", request.getRideId());

        Optional<Payment> existing =
                paymentRepository.findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {

            log.info(
                    "Duplicate payment request detected. Returning payment {}",
                    existing.get().getId()
            );

            return mapToResponse(existing.get());
        }

        Payment payment = Payment.builder()
                .rideId(request.getRideId())
                .payerId(request.getPayerId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .transactionId(TransactionIdGenerator.generateId())
                .gateway("MOCK_GATEWAY")
                .gatewayOrderId(transactionIdGenerator.generateGatewayOrderId())
                .gatewayPaymentId(transactionIdGenerator.generateGatewayPaymentId())
                .receiptNumber(receiptGenerator.generateReceipt())
                .processedAt(null)
                .failureReason(null)
                .idempotencyKey(idempotencyKey)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        paymentEventPublisher.publishPaymentCreated(

                PaymentCreatedEvent.builder()
                        .paymentId(savedPayment.getId())
                        .rideId(savedPayment.getRideId())
                        .payerId(savedPayment.getPayerId())
                        .amount(savedPayment.getAmount())
                        .currency(savedPayment.getCurrency())
                        .transactionId(savedPayment.getTransactionId())
                        .createdAt(savedPayment.getCreatedAt())
                        .status(savedPayment.getStatus())
                        .build()
        );

        auditService.log(
                savedPayment.getId(),
                savedPayment.getRideId(),
                AuditEvent.PAYMENT_CREATED,
                "SYSTEM",
                "Payment request created."
        );

        int attempt = 1;

        while (attempt <= maxRetryAttempts) {

            log.info(
                    "Payment attempt {} for transaction {}",
                    attempt,
                    savedPayment.getTransactionId()
            );

            try {

                savedPayment = paymentGatewayService.processPayment(savedPayment);

            } catch (GatewayTimeoutException ex) {

                savedPayment.setStatus(PaymentStatus.TIMEOUT);
                savedPayment.setFailureReason(ex.getMessage());

                savedPayment = paymentRepository.save(savedPayment);

                paymentEventPublisher.publishPaymentTimeout(

                        PaymentTimeoutEvent.builder()
                                .paymentId(savedPayment.getId())
                                .rideId(savedPayment.getRideId())
                                .transactionId(savedPayment.getTransactionId())
                                .timeoutAt(LocalDateTime.now())
                                .status(savedPayment.getStatus())
                                .build()
                );

                auditService.log(
                        savedPayment.getId(),
                        savedPayment.getRideId(),
                        AuditEvent.PAYMENT_TIMEOUT,
                        "SYSTEM",
                        ex.getMessage()
                );
            }

            // ------------------------------
            // Success
            // ------------------------------
            if (savedPayment.getStatus() == PaymentStatus.SUCCESS) {

                auditService.log(
                        savedPayment.getId(),
                        savedPayment.getRideId(),
                        AuditEvent.PAYMENT_SUCCESS,
                        "SYSTEM",
                        "Payment completed successfully on attempt " + attempt
                );

                paymentEventPublisher.publishPaymentSucceeded(

                        PaymentSucceededEvent.builder()
                                .paymentId(savedPayment.getId())
                                .rideId(savedPayment.getRideId())
                                .amount(savedPayment.getAmount())
                                .transactionId(savedPayment.getTransactionId())
                                .receiptNumber(savedPayment.getReceiptNumber())
                                .processedAt(savedPayment.getProcessedAt())
                                .status(savedPayment.getStatus())
                                .build()
                );

                return mapToResponse(savedPayment);
            }

            // ------------------------------
            // Retry only FAILED/TIMEOUT
            // ------------------------------
            if (savedPayment.getStatus() == PaymentStatus.FAILED
                    || savedPayment.getStatus() == PaymentStatus.TIMEOUT) {

                log.warn(
                        "Payment attempt {} ended with {}",
                        attempt,
                        savedPayment.getStatus()
                );

                if (attempt < maxRetryAttempts) {

                    auditService.log(
                            savedPayment.getId(),
                            savedPayment.getRideId(),
                            AuditEvent.PAYMENT_RETRY,
                            "SYSTEM",
                            "Retrying payment. Attempt " + (attempt + 1)
                    );

                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ex);
                    }
                }

                attempt++;
                continue;
            }

            // Any unexpected state
            break;
        }

        // ------------------------------
        // Final Failure Audit
        // ------------------------------

        paymentEventPublisher.publishPaymentFailed(

                PaymentFailedEvent.builder()
                        .paymentId(savedPayment.getId())
                        .rideId(savedPayment.getRideId())
                        .transactionId(savedPayment.getTransactionId())
                        .reason(savedPayment.getFailureReason())
                        .failedAt(LocalDateTime.now())
                        .status(savedPayment.getStatus())
                        .build()
        );

        auditService.log(
                savedPayment.getId(),
                savedPayment.getRideId(),
                AuditEvent.PAYMENT_FAILED,
                "SYSTEM",
                savedPayment.getFailureReason() != null
                        ? savedPayment.getFailureReason()
                        : "Payment failed after " + maxRetryAttempts + " attempts."
        );

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
                .gatewayOrderId(payment.getGatewayOrderId())
                .gatewayPaymentId(payment.getGatewayPaymentId())
                .receiptNumber(payment.getReceiptNumber())
                .gatewayResponseCode(payment.getGatewayResponseCode())
                .gatewayMessage(payment.getGatewayMessage())
                .failureReason(payment.getFailureReason())
                .processedAt(payment.getProcessedAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
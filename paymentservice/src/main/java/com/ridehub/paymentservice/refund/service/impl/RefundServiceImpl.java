package com.ridehub.paymentservice.refund.service.impl;

import com.ridehub.paymentservice.audit.enums.AuditEvent;
import com.ridehub.paymentservice.audit.service.interfaces.AuditService;
import com.ridehub.paymentservice.client.RideClient;
import com.ridehub.paymentservice.client.dto.request.UpdatePaymentStatusRequest;
import com.ridehub.paymentservice.entity.Payment;
import com.ridehub.paymentservice.enums.PaymentStatus;
import com.ridehub.paymentservice.exception.BadRequestException;
import com.ridehub.paymentservice.exception.BusinessRuleViolationException;
import com.ridehub.paymentservice.exception.ResourceNotFoundException;
import com.ridehub.paymentservice.kafka.dto.PaymentRefundFailedEvent;
import com.ridehub.paymentservice.kafka.dto.PaymentRefundedEvent;
import com.ridehub.paymentservice.kafka.publisher.PaymentEventPublisher;
import com.ridehub.paymentservice.refund.dto.request.RefundRequest;
import com.ridehub.paymentservice.refund.dto.response.RefundResponse;
import com.ridehub.paymentservice.refund.entity.Refund;
import com.ridehub.paymentservice.refund.enums.RefundStatus;
import com.ridehub.paymentservice.refund.repository.RefundRepository;
import com.ridehub.paymentservice.refund.service.interfaces.RefundGatewayService;
import com.ridehub.paymentservice.refund.service.interfaces.RefundService;
import com.ridehub.paymentservice.repository.PaymentRepository;
import com.ridehub.paymentservice.util.TransactionIdGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RefundServiceImpl implements RefundService {

    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final TransactionIdGenerator transactionIdGenerator;
    private final RefundGatewayService refundGatewayService;
    private final AuditService auditService;
    private final PaymentEventPublisher paymentEventPublisher;

    @Override
    public RefundResponse createRefund(
            Long paymentId,
            RefundRequest request) {

        log.info("Fetching transaction details for payment: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found."));

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new BadRequestException("Payment is already refunded.");
        }

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new BusinessRuleViolationException(
                    "Only successful payments can be refunded.");
        }

        if (request.getAmount().compareTo(payment.getAmount()) > 0) {
            throw new BusinessRuleViolationException(
                    "Refund amount exceeds payment amount.");
        }

        log.info("Creating refund request for ride {}", payment.getRideId());

        Refund refund = Refund.builder()
                .paymentId(payment.getId())
                .rideId(payment.getRideId())
                .amount(request.getAmount())
                .reason(request.getReason())
                .status(RefundStatus.PROCESSING)
                .refundTransactionId(
                        transactionIdGenerator.generateRefundTransactionId())
                .build();

        RefundStatus gatewayStatus =
                refundGatewayService.processRefund(refund);

        refund.setProcessedAt(LocalDateTime.now());

        if (gatewayStatus == RefundStatus.SUCCESS) {

            refund.setStatus(RefundStatus.SUCCESS);

            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setProcessedAt(LocalDateTime.now());

            paymentRepository.save(payment);

            refund = refundRepository.save(refund);

            paymentEventPublisher.publishPaymentRefunded(

                    PaymentRefundedEvent.builder()
                            .refundId(refund.getId())
                            .paymentId(refund.getPaymentId())
                            .rideId(refund.getRideId())
                            .amount(refund.getAmount())
                            .refundTransactionId(refund.getRefundTransactionId())
                            .processedAt(refund.getProcessedAt())
                            .build()
            );

            auditService.log(
                    payment.getId(),
                    payment.getRideId(),
                    AuditEvent.PAYMENT_REFUNDED,
                    "SYSTEM",
                    "Refund processed successfully."
            );

        } else {

            refund.setStatus(RefundStatus.FAILED);

            refund = refundRepository.save(refund);

            paymentEventPublisher.publishPaymentRefundFailed(

                    PaymentRefundFailedEvent.builder()
                            .refundId(refund.getId())
                            .paymentId(refund.getPaymentId())
                            .rideId(refund.getRideId())
                            .amount(refund.getAmount())
                            .refundTransactionId(refund.getRefundTransactionId())
                            .processedAt(refund.getProcessedAt())
                            .build()
            );

            auditService.log(
                    payment.getId(),
                    payment.getRideId(),
                    AuditEvent.REFUND_FAILED,
                    "SYSTEM",
                    "Refund gateway rejected the request."
            );
        }

        log.info("Refund created successfully.");

        return mapToResponse(refund);
    }

    @Override
    public RefundResponse getRefund(Long refundId) {

        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new ResourceNotFoundException("Refund not found with id : " + refundId));

        return mapToResponse(refund);
    }

    @Override
    public List<RefundResponse> getRefundsByPayment(Long paymentId) {
        return refundRepository.findByPaymentId(paymentId)
                .stream()
                .map(this :: mapToResponse)
                .toList();
    }

    @Override
    public List<RefundResponse> getRefundsByRide(Long rideId) {
        return refundRepository.findByRideId(rideId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private RefundResponse mapToResponse(Refund refund){
        return RefundResponse.builder()
                .id(refund.getId())
                .rideId(refund.getRideId())
                .paymentId(refund.getPaymentId())
                .reason(refund.getReason())
                .refundTransactionId(refund.getRefundTransactionId())
                .amount(refund.getAmount())
                .status(refund.getStatus())
                .processedAt(refund.getProcessedAt())
                .createdAt(refund.getCreatedAt())
                .build();
    }
}

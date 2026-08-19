package com.ridehub.paymentservice.kafka.consumer;

import com.ridehub.paymentservice.dto.request.PaymentRequest;
import com.ridehub.paymentservice.entity.Payment;
import com.ridehub.paymentservice.enums.PaymentStatus;
import com.ridehub.paymentservice.exception.ResourceNotFoundException;
import com.ridehub.paymentservice.kafka.dto.RideCancelledEvent;
import com.ridehub.paymentservice.kafka.dto.RideCompletedEvent;
import com.ridehub.paymentservice.refund.dto.request.RefundRequest;
import com.ridehub.paymentservice.refund.service.interfaces.RefundService;
import com.ridehub.paymentservice.repository.PaymentRepository;
import com.ridehub.paymentservice.service.interfaces.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RideEventConsumer {

    private final PaymentService paymentService;
    private final RefundService refundService;
    private final PaymentRepository paymentRepository;

    @KafkaListener(
            topics = "ride-completed",
            groupId = "payment-service")
    public void consumeRideCompleted(
            RideCompletedEvent event) {

        log.info(
                "Received RideCompletedEvent for ride {}",
                event.getRideId()
        );

        PaymentRequest request = PaymentRequest.builder()
                .rideId(event.getRideId())
                .payerId(event.getRiderId())
                .currency(event.getCurrency())
                .amount(event.getFare())
                .paymentMethod(event.getMethod())
                .build();

        String idempotencyKey =
                "ride:" + event.getRideId() + ":complete";

        paymentService.createPayment(
                request,
                idempotencyKey
        );
    }

    @KafkaListener(
            topics = "ride-cancelled",
            groupId = "payment-service")
    public void consumeRideCancelled(
            RideCancelledEvent event) {

        log.info(
                "Received RideCancelledEvent for ride {}",
                event.getRideId()
        );

        try {

            Payment payment = paymentRepository
                    .findByRideId(event.getRideId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Payment not found for ride "
                                            + event.getRideId()));

            if (payment.getStatus() == PaymentStatus.SUCCESS) {

                RefundRequest refundRequest =
                        RefundRequest.builder()
                                .amount(payment.getAmount())
                                .reason("Ride Cancelled")
                                .build();

                refundService.createRefund(
                        payment.getId(),
                        refundRequest
                );

                log.info(
                        "Refund initiated for ride {}",
                        event.getRideId()
                );

            } else {

                log.info(
                        "No refund required. Payment status = {}",
                        payment.getStatus()
                );
            }

        } catch (Exception ex) {

            log.error(
                    "Refund processing failed for ride {}",
                    event.getRideId(),
                    ex
            );
        }
    }
}
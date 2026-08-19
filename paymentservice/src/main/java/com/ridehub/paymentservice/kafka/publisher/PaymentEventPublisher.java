package com.ridehub.paymentservice.kafka.publisher;

import com.ridehub.paymentservice.kafka.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentCreated(
            PaymentCreatedEvent event) {

        kafkaTemplate.send(
                "payment-created",
                event.getRideId().toString(),
                event
        );

        log.info(
                "Published PaymentCreatedEvent payment={} ride={}",
                event.getPaymentId(),
                event.getRideId()
        );
    }

    public void publishPaymentSucceeded(
            PaymentSucceededEvent event) {

        kafkaTemplate.send(
                "payment-succeeded",
                event.getRideId().toString(),
                event
        );

        log.info(
                "Published PaymentSucceededEvent payment={} ride={}",
                event.getPaymentId(),
                event.getRideId()
        );
    }

    public void publishPaymentFailed(
            PaymentFailedEvent event) {

        kafkaTemplate.send(
                "payment-failed",
                event.getRideId().toString(),
                event
        );

        log.info(
                "Published PaymentFailedEvent payment={} ride={}",
                event.getPaymentId(),
                event.getRideId()
        );
    }

    public void publishPaymentTimeout(
            PaymentTimeoutEvent event) {

        kafkaTemplate.send(
                "payment-timeout",
                event.getRideId().toString(),
                event
        );

        log.info(
                "Published PaymentTimeoutEvent payment={} ride={}",
                event.getPaymentId(),
                event.getRideId()
        );
    }

    public void publishPaymentRefunded(
            PaymentRefundedEvent event) {

        kafkaTemplate.send(
                "payment-refunded",
                event.getRideId().toString(),
                event
        );

        log.info(
                "Published PaymentRefundedEvent payment={} ride={}",
                event.getPaymentId(),
                event.getRideId()
        );
    }

    public void publishPaymentRefundFailed(
            PaymentRefundFailedEvent event
    ){
        kafkaTemplate.send(
                "payment-refund-failed",
                event.getRideId().toString(),
                event
        );

        log.info(
                "Published PaymentRefundFailedEvent payment={} ride={}",
                event.getPaymentId(),
                event.getRideId()
        );
    }
}
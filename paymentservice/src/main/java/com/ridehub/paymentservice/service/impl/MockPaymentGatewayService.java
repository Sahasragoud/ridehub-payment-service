package com.ridehub.paymentservice.service.impl;

import com.ridehub.paymentservice.dto.response.GatewayResponse;
import com.ridehub.paymentservice.entity.Payment;
import com.ridehub.paymentservice.enums.PaymentStatus;
import com.ridehub.paymentservice.repository.PaymentRepository;
import com.ridehub.paymentservice.service.interfaces.PaymentGatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class MockPaymentGatewayService implements PaymentGatewayService {

    private final PaymentRepository paymentRepository;

    private final Random random = new Random();

    @Override
    public Payment processPayment(Payment payment) {

        log.info("Processing payment {}", payment.getTransactionId());

        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setProcessedAt(LocalDateTime.now());
        payment.setGatewayResponseCode(GatewayResponse.PROCESSING_CODE);
        payment.setGatewayMessage(GatewayResponse.PROCESSING_MESSAGE);

        paymentRepository.save(payment);

        // Simulate gateway delay
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int result = random.nextInt(100);

        if (result < 80) {

            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setGatewayResponseCode(GatewayResponse.SUCCESS_CODE);
            payment.setGatewayMessage(GatewayResponse.SUCCESS_MESSAGE);

            log.info("Payment {} completed successfully.",
                    payment.getTransactionId());

        } else {

            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Mock gateway declined the payment.");
            payment.setGatewayResponseCode(GatewayResponse.FAILED_CODE);
            payment.setGatewayMessage(GatewayResponse.FAILED_MESSAGE);

            log.warn("Payment {} failed.",
                    payment.getTransactionId());
        }

        return paymentRepository.save(payment);

    }

}
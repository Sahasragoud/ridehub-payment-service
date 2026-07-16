package com.ridehub.paymentservice.service.impl;

import com.ridehub.paymentservice.entity.Payment;
import com.ridehub.paymentservice.enums.PaymentStatus;
import com.ridehub.paymentservice.repository.PaymentRepository;
import com.ridehub.paymentservice.service.interfaces.PaymentGatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

            log.info("Payment {} completed successfully.",
                    payment.getTransactionId());

        } else {

            payment.setStatus(PaymentStatus.FAILED);

            payment.setFailureReason("Mock gateway declined the payment.");

            log.warn("Payment {} failed.",
                    payment.getTransactionId());
        }

        return paymentRepository.save(payment);

    }

}
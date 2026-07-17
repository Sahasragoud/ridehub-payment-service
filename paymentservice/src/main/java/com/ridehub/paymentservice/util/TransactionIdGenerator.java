package com.ridehub.paymentservice.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public final class TransactionIdGenerator {

    private TransactionIdGenerator() {
    }

    public static String generateId() {
        return "TXN-" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 12)
                        .toUpperCase();
    }

    public String generateGatewayOrderId() {
        return "ORD-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }

    public String generateGatewayPaymentId() {
        return "PAY-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }
}
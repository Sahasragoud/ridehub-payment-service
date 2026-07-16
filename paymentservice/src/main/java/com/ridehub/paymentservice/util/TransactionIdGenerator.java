package com.ridehub.paymentservice.util;

import java.util.UUID;

public final class TransactionIdGenerator {

    private TransactionIdGenerator() {
    }

    public static String generate() {
        return "TXN-" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 12)
                        .toUpperCase();
    }

}
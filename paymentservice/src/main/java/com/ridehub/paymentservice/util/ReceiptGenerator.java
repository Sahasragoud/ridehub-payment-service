package com.ridehub.paymentservice.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class ReceiptGenerator {

    private final AtomicLong counter = new AtomicLong(1);

    public String generateReceipt() {

        String date = LocalDate.now()
                .format(DateTimeFormatter.BASIC_ISO_DATE);

        return "RCPT-" + date + "-"
                + String.format("%06d", counter.getAndIncrement());
    }
}
package com.ridehub.paymentservice.audit.enums;

public enum AuditEvent {

    PAYMENT_CREATED,
    PAYMENT_SUCCESS,
    PAYMENT_FAILED,
    PAYMENT_RETRY,
    PAYMENT_REFUNDED,
    REFUND_CREATED,
    REFUND_SUCCESS,
    REFUND_FAILED,
    PAYMENT_TIMEOUT
}
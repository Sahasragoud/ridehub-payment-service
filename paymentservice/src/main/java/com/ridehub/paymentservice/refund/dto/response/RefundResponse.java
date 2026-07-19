package com.ridehub.paymentservice.refund.dto.response;

import com.ridehub.paymentservice.refund.enums.RefundStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class RefundResponse {

    private Long id;

    private Long paymentId;

    private Long rideId;

    private BigDecimal amount;

    private String reason;

    private RefundStatus status;

    private String refundTransactionId;

    private LocalDateTime createdAt;

    private LocalDateTime processedAt;

}
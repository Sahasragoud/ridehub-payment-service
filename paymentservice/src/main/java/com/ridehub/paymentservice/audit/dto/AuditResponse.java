package com.ridehub.paymentservice.audit.dto;

import com.ridehub.paymentservice.audit.enums.AuditEvent;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuditResponse {
    Long id;

    Long paymentId;

    Long rideId;

    AuditEvent event;

    String performedBy;

    String details;

    LocalDateTime createdAt;
}

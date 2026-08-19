package com.ridehub.paymentservice.audit.entity;

import com.ridehub.paymentservice.audit.enums.AuditEvent;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long paymentId;

    private Long rideId;

    @Enumerated(EnumType.STRING)
    private AuditEvent event;

    private String performedBy;

    @Column(length = 1000)
    private String details;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

}

package com.ridehub.paymentservice.audit.repository;

import com.ridehub.paymentservice.audit.entity.AuditLog;
import com.ridehub.paymentservice.audit.enums.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditRepository
        extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByPaymentId(Long paymentId);

    List<AuditLog> findByRideId(Long rideId);

    List<AuditLog> findByEvent(AuditEvent event);

    List<AuditLog> findAllByOrderByCreatedAtDesc();
}
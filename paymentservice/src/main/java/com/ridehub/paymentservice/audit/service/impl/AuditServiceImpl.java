package com.ridehub.paymentservice.audit.service.impl;

import com.ridehub.paymentservice.audit.dto.AuditResponse;
import com.ridehub.paymentservice.audit.entity.AuditLog;
import com.ridehub.paymentservice.audit.enums.AuditEvent;
import com.ridehub.paymentservice.audit.repository.AuditRepository;
import com.ridehub.paymentservice.audit.service.interfaces.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;

    @Override
    public void log(
            Long paymentId,
            Long rideId,
            AuditEvent event,
            String performedBy,
            String details) {

        AuditLog audit = AuditLog.builder()
                .paymentId(paymentId)
                .rideId(rideId)
                .event(event)
                .performedBy(performedBy)
                .details(details)
                .build();

        auditRepository.save(audit);
    }

    @Override
    public List<AuditResponse> getAllLogs() {
        return auditRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<AuditResponse> getLogsByPayment(Long paymentId) {
        return auditRepository.findByPaymentId(paymentId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<AuditResponse> getLogsByRide(Long rideId) {
        return auditRepository.findByRideId(rideId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<AuditResponse> getLogsByEvent(AuditEvent event) {
        return auditRepository.findByEvent(event)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AuditResponse mapToResponse(AuditLog log){
        return AuditResponse.builder()
                .id(log.getId())
                .createdAt(log.getCreatedAt())
                .performedBy(log.getPerformedBy())
                .details(log.getDetails())
                .event(log.getEvent())
                .paymentId(log.getPaymentId())
                .rideId(log.getRideId())
                .build();
    }
}

package com.ridehub.paymentservice.audit.service.interfaces;

import com.ridehub.paymentservice.audit.dto.AuditResponse;
import com.ridehub.paymentservice.audit.enums.AuditEvent;

import java.util.List;

public interface AuditService {

    void log(
            Long paymentId,
            Long rideId,
            AuditEvent event,
            String performedBy,
            String details
    );

    List<AuditResponse> getAllLogs();

    List<AuditResponse> getLogsByPayment(Long paymentId);

    List<AuditResponse> getLogsByRide(Long rideId);

    List<AuditResponse> getLogsByEvent(AuditEvent event);

}

package com.ridehub.paymentservice.audit.controller;

import com.ridehub.paymentservice.audit.dto.AuditResponse;
import com.ridehub.paymentservice.audit.enums.AuditEvent;
import com.ridehub.paymentservice.audit.service.interfaces.AuditService;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController{

    private final AuditService auditService;

    @GetMapping
    public List<AuditResponse> getAllLogs(){
        return auditService.getAllLogs();
    }

    @GetMapping("/payment/{paymentId}")
    public List<AuditResponse> getLogsByPayment(Long paymentId){
        return auditService.getLogsByPayment(paymentId);
    }

    @GetMapping("/event/{event}")
    public List<AuditResponse> getLogsByRide(Long rideId){
        return auditService.getLogsByRide(rideId);
    }

    @GetMapping("/ride/{rideId}")
    public List<AuditResponse> getLogsByEvent(AuditEvent event){
        return auditService.getLogsByEvent(event);
    }


}
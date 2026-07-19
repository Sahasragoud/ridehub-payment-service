package com.ridehub.paymentservice.refund.controller;

import com.ridehub.paymentservice.refund.dto.request.RefundRequest;
import com.ridehub.paymentservice.refund.dto.response.RefundResponse;
import com.ridehub.paymentservice.refund.service.interfaces.RefundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @PostMapping("/payment/{paymentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public RefundResponse createRefund(
            @PathVariable Long paymentId,
            @Valid @RequestBody RefundRequest request) {

        return refundService.createRefund(paymentId, request);
    }
}

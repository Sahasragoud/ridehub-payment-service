package com.ridehub.paymentservice.refund.service.impl;

import com.ridehub.paymentservice.refund.entity.Refund;
import com.ridehub.paymentservice.refund.enums.RefundStatus;
import com.ridehub.paymentservice.refund.service.interfaces.RefundGatewayService;
import org.springframework.stereotype.Service;

@Service
public class MockRefundGatewayService
        implements RefundGatewayService {

    @Override
    public RefundStatus processRefund(Refund refund) {

        return RefundStatus.SUCCESS;

    }
}
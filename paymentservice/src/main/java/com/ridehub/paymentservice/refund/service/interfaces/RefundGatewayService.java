package com.ridehub.paymentservice.refund.service.interfaces;

import com.ridehub.paymentservice.refund.entity.Refund;
import com.ridehub.paymentservice.refund.enums.RefundStatus;

public interface RefundGatewayService {

    RefundStatus processRefund(Refund refund);

}
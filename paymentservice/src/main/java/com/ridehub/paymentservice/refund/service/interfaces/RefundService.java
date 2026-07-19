package com.ridehub.paymentservice.refund.service.interfaces;

import com.ridehub.paymentservice.refund.dto.request.RefundRequest;
import com.ridehub.paymentservice.refund.dto.response.RefundResponse;
import org.springframework.stereotype.Service;

import java.util.List;


public interface RefundService {

    RefundResponse createRefund(
            Long paymentId,
            RefundRequest request
    );

    RefundResponse getRefund(Long refundId);

    List<RefundResponse> getRefundsByPayment(Long paymentId);

    List<RefundResponse> getRefundsByRide(Long rideId);

}
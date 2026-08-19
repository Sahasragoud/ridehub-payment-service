package com.ridehub.paymentservice.client;

import com.ridehub.paymentservice.client.dto.request.UpdatePaymentStatusRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "rideservice",
        url = "${ride.service.url}"
)
public interface RideClient {

    @PutMapping("/api/rides/internal/rides/{rideId}/payment-status")    void updatePaymentStatus(
            @PathVariable Long rideId,
            @RequestBody UpdatePaymentStatusRequest request
    );

}
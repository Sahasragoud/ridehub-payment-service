package com.ridehub.paymentservice.kafka.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideCancelledEvent {

    private Long rideId;

    private Long riderId;

    private Long driverId;

    private String reason;

    private LocalDateTime cancelledAt;

}

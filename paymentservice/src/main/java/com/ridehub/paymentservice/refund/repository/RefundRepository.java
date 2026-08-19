package com.ridehub.paymentservice.refund.repository;

import com.ridehub.paymentservice.refund.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {

    List<Refund> findByPaymentId(Long paymentId);

    List<Refund> findByRideId(Long rideId);

}
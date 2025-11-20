package com.example.elitedriverbackend.repositories;

import com.example.elitedriverbackend.domain.entity.Reservation;
import com.example.elitedriverbackend.domain.entity.ReservationStatus;
import com.example.elitedriverbackend.domain.entity.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    @Query("""
        SELECT r FROM Reservation r
        WHERE r.vehicle.id = :vehicleId
          AND r.status IN (com.example.elitedriverbackend.domain.entity.ReservationStatus.CONFIRMED)
          AND (:startDate <= r.endDate AND :endDate >= r.startDate)
        """)
    List<Reservation> findConfirmedOverlap(UUID vehicleId, Date startDate, Date endDate);

    List<Reservation> findByVehicle_VehicleType(VehicleType vehicleType);

    Optional<Reservation> findByPaymentReference(String paymentReference);

    @Query("""
        SELECT r FROM Reservation r
        WHERE r.status = com.example.elitedriverbackend.domain.entity.ReservationStatus.PENDING_PAYMENT
          AND r.expiresAt < CURRENT_TIMESTAMP
        """)
    List<Reservation> findExpiredPendings();
}
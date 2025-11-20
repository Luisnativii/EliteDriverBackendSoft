package com.example.elitedriverbackend.repositories;

import com.example.elitedriverbackend.domain.entity.Vehicle;
import com.example.elitedriverbackend.domain.entity.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    @Query("""
        SELECT v
        FROM Vehicle v
        WHERE v.status = :status
          AND v.id NOT IN (
            SELECT r.vehicle.id
            FROM Reservation r
            WHERE r.status = com.example.elitedriverbackend.domain.entity.ReservationStatus.CONFIRMED
              AND r.startDate <= :endDate
              AND r.endDate   >= :startDate
          )
        """)
    List<Vehicle> findAvailableBetween(VehicleStatus status, Date startDate, Date endDate);
}
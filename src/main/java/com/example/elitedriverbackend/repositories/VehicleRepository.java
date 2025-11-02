package com.example.elitedriverbackend.repositories;

import com.example.elitedriverbackend.domain.entity.Vehicle;
import com.example.elitedriverbackend.domain.entity.VehicleStatus;
import com.example.elitedriverbackend.domain.entity.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    List<Vehicle> findByVehicleType(VehicleType vehicleType); // Cambiado de findByCarType

    List<Vehicle> findByCapacity(int capacity);

    @Query("""
        SELECT v
        FROM Vehicle v
        WHERE v.status = :status
          AND v.id NOT IN (
            SELECT r.vehicle.id
            FROM Reservation r
            WHERE r.startDate <= :endDate
              AND r.endDate   >= :startDate
          )
        """)
    List<Vehicle> findAvailableBetween(
            @Param("status")     VehicleStatus status,
            @Param("startDate")  Date startDate,
            @Param("endDate") Date endDate
    );

}
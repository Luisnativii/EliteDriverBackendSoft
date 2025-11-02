package com.example.elitedriverbackend.repositories;

import com.example.elitedriverbackend.domain.entity.Reservation;
import com.example.elitedriverbackend.domain.entity.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findByStartDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    List<Reservation> findByVehicle_VehicleType(VehicleType vehicleType);
    @Query("SELECT r FROM Reservation r WHERE r.vehicle.id = :vehicleId AND " +
            "(:startDate <= r.endDate AND :endDate >= r.startDate)")
    List<Reservation> findByVehicleIdAndDateOverlap(
            @Param("vehicleId") UUID vehicleId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

}

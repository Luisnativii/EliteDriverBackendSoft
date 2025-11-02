package com.example.elitedriverbackend.repositories;

import com.example.elitedriverbackend.domain.entity.MaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface MaintenanceRecordRepository
        extends JpaRepository<MaintenanceRecord, UUID> {
    List<MaintenanceRecord> findByVehicleIdOrderByMaintenanceDateDesc(UUID vehicleId);
}


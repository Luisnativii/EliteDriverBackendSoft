package com.example.elitedriverbackend.services;

import com.example.elitedriverbackend.domain.entity.MaintenanceRecord;
import com.example.elitedriverbackend.domain.entity.Vehicle;
import com.example.elitedriverbackend.domain.entity.VehicleStatus;
import com.example.elitedriverbackend.repositories.MaintenanceRecordRepository;
import com.example.elitedriverbackend.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaintenanceService {

    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final VehicleRepository vehicleRepository;


    @Transactional
    public void processKilometerUpdate(Vehicle vehicle, Integer newKilometers, VehicleStatus requestedStatus) {
        if (newKilometers == null) return;

        Integer prevKmObj = vehicle.getKilometers();
        int prevKm = (prevKmObj != null) ? prevKmObj : 0;
        vehicle.setKilometers(newKilometers);

        Integer intervalObj = vehicle.getKmForMaintenance();
        if (intervalObj == null || intervalObj <= 0) {
            if (requestedStatus != null) vehicle.setStatus(requestedStatus);
            return;
        }

        // Si el nuevo km es menor (corrección manual), no disparar mantenimiento automático
        if (newKilometers < prevKm) {
            if (requestedStatus != null) vehicle.setStatus(requestedStatus);
            return;
        }

        int interval   = intervalObj;
        int prevCycles = prevKm / interval;
        int currCycles = newKilometers / interval;

        if (currCycles > prevCycles) {
            MaintenanceRecord record = MaintenanceRecord.builder()
                    .vehicle(vehicle)
                    .maintenanceDate(LocalDateTime.now())
                    .kmAtMaintenance(newKilometers)
                    .build();
            maintenanceRecordRepository.save(record);
            vehicle.setStatus(VehicleStatus.maintenanceRequired);
            log.debug("Maintenance requerido para '{}' (km={}, intervalo={})", vehicle.getName(), newKilometers, interval);
        } else if (requestedStatus != null) {
            vehicle.setStatus(requestedStatus);
        }
    }

    @Transactional
    public void markMaintenanceCompleted(UUID vehicleId) {
        Vehicle v = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle no encontrado"));
        v.setStatus(VehicleStatus.maintenanceCompleted);
        vehicleRepository.save(v);
    }

    @Transactional
    public MaintenanceRecord createManualRecord(UUID vehicleId, Integer km, LocalDateTime dateTime) {
        Vehicle v = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle no encontrado"));
        MaintenanceRecord record = MaintenanceRecord.builder()
                .vehicle(v)
                .maintenanceDate(dateTime != null ? dateTime : LocalDateTime.now())
                .kmAtMaintenance(km != null ? km : v.getKilometers())
                .build();
        maintenanceRecordRepository.save(record);
        v.setStatus(VehicleStatus.maintenanceRequired);
        vehicleRepository.save(v);
        return record;
    }

    @Transactional(readOnly = true)
    public List<MaintenanceRecord> getHistory(UUID vehicleId) {
        return maintenanceRecordRepository.findByVehicleIdOrderByMaintenanceDateDesc(vehicleId);
    }
}
package com.example.elitedriverbackend.services;

import com.example.elitedriverbackend.domain.entity.MaintenanceRecord;
import com.example.elitedriverbackend.domain.entity.Vehicle;
import com.example.elitedriverbackend.domain.entity.VehicleStatus;
import com.example.elitedriverbackend.repositories.MaintenanceRecordRepository;
import com.example.elitedriverbackend.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaintenanceService {

    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final VehicleRepository vehicleRepository;

    /**
     * Aplica la lógica de detección de mantenimiento cuando se actualizan kilómetros.
     * Mantiene el comportamiento anterior:
     * - Si se cruza un nuevo múltiplo de kmForMaintenance → crea registro y marca maintenanceRequired.
     * - Si no se cruza y llega un estado explícito, se aplica ese estado.
     */
    public void processKilometerUpdate(Vehicle vehicle, Integer newKilometers, VehicleStatus requestedStatus) {
        Integer prevKm = vehicle.getKilometers();
        vehicle.setKilometers(newKilometers);

        if (vehicle.getKmForMaintenance() == null) {
            if (requestedStatus != null) vehicle.setStatus(requestedStatus);
            return;
        }

        int interval   = vehicle.getKmForMaintenance();
        int prevCycles = prevKm / interval;
        int currCycles = newKilometers / interval;

        log.info("🔧 Verificando mantenimiento '{}' ({}→{} km) interval {}", vehicle.getName(), prevKm, newKilometers, interval);

        if (currCycles > prevCycles) {
            MaintenanceRecord record = MaintenanceRecord.builder()
                    .vehicle(vehicle)
                    .maintenanceDate(LocalDateTime.now())
                    .kmAtMaintenance(newKilometers)
                    .build();
            maintenanceRecordRepository.save(record);
            vehicle.setStatus(VehicleStatus.maintenanceRequired);
            log.info("⚠️ '{}' requiere mantenimiento (registro creado)", vehicle.getName());
        } else if (requestedStatus != null) {
            vehicle.setStatus(requestedStatus);
        }
    }

    /**
     * Marca que el mantenimiento fue realizado (simple cambio de estado).
     */
    public void markMaintenanceCompleted(UUID vehicleId) {
        Vehicle v = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle no encontrado"));
        v.setStatus(VehicleStatus.maintenanceCompleted);
        vehicleRepository.save(v);
        log.info("✅ Mantenimiento completado para '{}'", v.getName());
    }

    /**
     * Crea un registro manual (por ejemplo si se ingresa desde un taller externo) y marca estado requerido.
     */
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

    public List<MaintenanceRecord> getHistory(UUID vehicleId) {
        return maintenanceRecordRepository.findByVehicleIdOrderByMaintenanceDateDesc(vehicleId);
    }
}
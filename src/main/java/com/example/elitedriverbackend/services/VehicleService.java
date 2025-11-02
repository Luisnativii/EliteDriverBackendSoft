package com.example.elitedriverbackend.services;

import com.example.elitedriverbackend.domain.dtos.CreateVehicleDTO;
import com.example.elitedriverbackend.domain.dtos.MaintenanceRecordDTO;
import com.example.elitedriverbackend.domain.dtos.UpdateVehicleDTO;
import com.example.elitedriverbackend.domain.dtos.VehicleResponseDTO;
import com.example.elitedriverbackend.domain.dtos.VehicleTypeDTO;
import com.example.elitedriverbackend.domain.entity.MaintenanceRecord;
import com.example.elitedriverbackend.domain.entity.Vehicle;
import com.example.elitedriverbackend.domain.entity.VehicleStatus;
import com.example.elitedriverbackend.domain.entity.VehicleType;
import com.example.elitedriverbackend.repositories.MaintenanceRecordRepository;
import com.example.elitedriverbackend.repositories.VehicleRepository;
import com.example.elitedriverbackend.repositories.VehicleTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@Slf4j
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;

    @Autowired
    private MaintenanceRecordRepository maintenanceRecordRepository;

    public void addVehicle(CreateVehicleDTO dto) {
        Vehicle v = new Vehicle();
        v.setName(dto.getName());
        v.setBrand(dto.getBrand());
        v.setModel(dto.getModel());
        v.setCapacity(dto.getCapacity());
        v.setPricePerDay(dto.getPricePerDay());
        v.setKilometers(dto.getKilometers());
        v.setFeatures(dto.getFeatures());
        v.setInsurancePhone(dto.getInsurancePhone());
        v.setKmForMaintenance(dto.getKmForMaintenance());
        v.setStatus(VehicleStatus.maintenanceCompleted);
        v.setMainImageUrl(dto.getMainImageUrl());
        if (dto.getImageUrls() != null) {
            v.setImageUrls(new ArrayList<>(dto.getImageUrls()));
        }

        String typeName = dto.getVehicleType().getType();
        VehicleType type = vehicleTypeRepository.findByType(typeName)
                .orElseThrow(() -> new RuntimeException("Vehicle type '" + typeName + "' no encontrado"));
        v.setVehicleType(type);

        vehicleRepository.save(v);
    }

    public void updateVehicle(UpdateVehicleDTO dto, UUID id) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle con id " + id + " no encontrado"));
        Integer prevKm = v.getKilometers();

        // Update simple fields
        if (dto.getPricePerDay() != null)        v.setPricePerDay(dto.getPricePerDay());
        if (dto.getKilometers() != null)         v.setKilometers(dto.getKilometers());
        if (dto.getFeatures() != null)           v.setFeatures(dto.getFeatures());
        if (dto.getInsurancePhone() != null)     v.setInsurancePhone(dto.getInsurancePhone());
        if (dto.getKmForMaintenance() != null)   v.setKmForMaintenance(dto.getKmForMaintenance());
        if (dto.getMainImageUrl() != null)       v.setMainImageUrl(dto.getMainImageUrl());
        if (dto.getImageUrls() != null)          v.setImageUrls(new ArrayList<>(dto.getImageUrls()));

        // Maintenance logic: record date+km when crossing a maintenance interval
        if (dto.getKilometers() != null && v.getKmForMaintenance() != null) {
            int currKm      = dto.getKilometers();
            int interval    = v.getKmForMaintenance();
            int prevCycles  = prevKm / interval;
            int currCycles  = currKm / interval;

            log.info("🔧 Checking maintenance for '{}' ({}→{} km), interval {}",
                    v.getName(), prevKm, currKm, interval);

            if (currCycles > prevCycles) {
                // Persist maintenance record
                MaintenanceRecord record = MaintenanceRecord.builder()
                        .vehicle(v)
                        .maintenanceDate(LocalDateTime.now())
                        .kmAtMaintenance(currKm)
                        .build();
                maintenanceRecordRepository.save(record);

                v.setStatus(VehicleStatus.maintenanceRequired);
                log.info("⚠️ '{}' now requires maintenance", v.getName());
            }
            else if (dto.getStatus() != null) {
                v.setStatus(dto.getStatus());
            }
        }
        else if (dto.getStatus() != null) {
            v.setStatus(dto.getStatus());
        }

        vehicleRepository.save(v);
    }

    public void deleteVehicle(UUID id) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle con id " + id + " no encontrado"));
        vehicleRepository.delete(v);
    }

    public List<VehicleResponseDTO> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public VehicleResponseDTO getVehicleById(UUID id) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle con id " + id + " no encontrado"));
        return toResponseDTO(v);
    }

    public List<VehicleResponseDTO> getVehicleByType(VehicleTypeDTO typeDto) {
        String typeName = typeDto.getType();
        VehicleType type = vehicleTypeRepository.findByType(typeName)
                .orElseThrow(() -> new RuntimeException("Vehicle type '" + typeName + "' no encontrado"));
        return vehicleRepository.findByVehicleType(type).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<VehicleResponseDTO> getVehicleByCapacity(String capacity) {
        int cap = Integer.parseInt(capacity);
        return vehicleRepository.findByCapacity(cap).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<VehicleResponseDTO> getAvailableVehicles(Date startDate, Date endDate) {
        return vehicleRepository.findAvailableBetween(
                        VehicleStatus.maintenanceCompleted,
                        startDate,
                        endDate
                ).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // —————— Helper: map Vehicle + its MaintenanceRecords → DTO ——————
    private VehicleResponseDTO toResponseDTO(Vehicle v) {
        List<MaintenanceRecordDTO> hist = maintenanceRecordRepository
                .findByVehicleIdOrderByMaintenanceDateDesc(v.getId())
                .stream()
                .map(r -> MaintenanceRecordDTO.builder()
                        .id(r.getId())
                        .maintenanceDate(r.getMaintenanceDate())
                        .kmAtMaintenance(r.getKmAtMaintenance())
                        .createdAt(r.getCreatedAt())
                        .updatedAt(r.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        return VehicleResponseDTO.builder()
                .id(v.getId().toString())
                .name(v.getName())
                .brand(v.getBrand())
                .model(v.getModel())
                .capacity(v.getCapacity())
                .pricePerDay(v.getPricePerDay())
                .kilometers(v.getKilometers())
                .features(v.getFeatures())
                .vehicleType(VehicleResponseDTO.VehicleTypeInfo.builder()
                        .id(v.getVehicleType().getId().toString())
                        .type(v.getVehicleType().getType())
                        .build())
                .kmForMaintenance(v.getKmForMaintenance())
                .status(v.getStatus())
                .mainImageUrl(v.getMainImageUrl())
                .imageUrls(v.getImageUrls())
                .insurancePhone(v.getInsurancePhone())
                .maintenanceRecords(hist)
                .build();
    }
}

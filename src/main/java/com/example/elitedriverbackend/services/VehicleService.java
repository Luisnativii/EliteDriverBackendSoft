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
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.sqm.EntityTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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


    @Transactional
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
        v.setStatus(VehicleStatus.maintenanceCompleted); // Inicialmente disponible post\-mantenimiento
        v.setMainImageBase64(dto.getMainImageBase64());

        if (dto.getListImagesBase64() != null) {
            v.setListImagesBase64(new ArrayList<>(dto.getListImagesBase64()));
        }

        String typeName = dto.getVehicleType().getType();
        VehicleType type = vehicleTypeRepository.findByType(typeName)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle type '" + typeName + "' no encontrado"));
        v.setVehicleType(type);

        vehicleRepository.save(v);
    }

    @Transactional
    public void updateVehicle(UpdateVehicleDTO dto, UUID id) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle con id " + id + " no encontrado"));

        Integer prevKm = v.getKilometers();

        if (dto.getPricePerDay() != null)        v.setPricePerDay(dto.getPricePerDay());
        if (dto.getKilometers() != null)         v.setKilometers(dto.getKilometers());
        if (dto.getFeatures() != null)           v.setFeatures(dto.getFeatures());
        if (dto.getInsurancePhone() != null)     v.setInsurancePhone(dto.getInsurancePhone());
        if (dto.getKmForMaintenance() != null)   v.setKmForMaintenance(dto.getKmForMaintenance());
        if (dto.getMainImageBase64() != null)    v.setMainImageBase64(dto.getMainImageBase64());
        if (dto.getListImagesBase64() != null)   v.setListImagesBase64(new ArrayList<>(dto.getListImagesBase64()));

        // Si el caller quiere forzar estado (solo permitido en estados no conflictivos)
        if (dto.getStatus() != null) {
            // Solo permitir ciertos cambios directos
            if (canApplyDirectStatus(dto.getStatus(), v.getStatus())) {
                v.setStatus(dto.getStatus());
            }
        }

        // Procesar mantenimiento si cambió kilometraje
        if (dto.getKilometers() != null && v.getKmForMaintenance() != null && prevKm != null) {
            handleMaintenanceOnKilometerChange(v, prevKm, dto.getKilometers());
        }

        vehicleRepository.save(v);
    }

    @Transactional
    public void deleteVehicle(UUID id) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle con id " + id + " no encontrado"));
        vehicleRepository.delete(v);
    }

    // =============================================
    // OPERACIONES DE MANTENIMIENTO
    // =============================================

    @Transactional
    public VehicleResponseDTO startMaintenance(UUID vehicleId) {
        Vehicle v = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado"));
        if (v.getStatus() != VehicleStatus.maintenanceRequired
                && v.getStatus() != VehicleStatus.maintenanceCompleted) {
            throw new EntityNotFoundException("No se puede iniciar mantenimiento desde estado: " + v.getStatus());
        }
        v.setStatus(VehicleStatus.underMaintenance);
        vehicleRepository.save(v);
        return toResponseDTO(v);
    }

    @Transactional
    public VehicleResponseDTO markMaintenanceCompleted(UUID vehicleId) {
        Vehicle v = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado"));
        if (v.getStatus() != VehicleStatus.underMaintenance
                && v.getStatus() != VehicleStatus.maintenanceRequired) {
            throw new EntityNotFoundException("No se puede completar mantenimiento desde estado: " + v.getStatus());
        }
        v.setStatus(VehicleStatus.maintenanceCompleted);
        vehicleRepository.save(v);
        return toResponseDTO(v);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceRecordDTO> getMaintenanceHistory(UUID vehicleId) {
        vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado"));
        return maintenanceRecordRepository
                .findByVehicleIdOrderByMaintenanceDateDesc(vehicleId)
                .stream()
                .map(this::toMaintenanceDTO)
                .collect(Collectors.toList());
    }

    // Lógica central para detectar nuevos ciclos
    private void handleMaintenanceOnKilometerChange(Vehicle v, int prevKm, int newKm) {
        int interval = v.getKmForMaintenance();
        if (interval == 0) return;

        int prevCycles = prevKm / interval;
        int newCycles = newKm / interval;

        if (newCycles <= prevCycles) {
            return; // No se cruzó nuevo umbral
        }

        log.info("🔧 Vehículo '{}' cruzó {} nuevo(s) ciclo(s) de mantenimiento ({}→{}, intervalo={})",
                v.getName(), (newCycles - prevCycles), prevKm, newKm, interval);

        // Crear un registro por cada ciclo nuevo alcanzado
        for (int cycle = prevCycles + 1; cycle <= newCycles; cycle++) {
            int cycleKm = cycle * interval;
            createMaintenanceRecordIfAbsent(v, cycleKm);
        }

        // Estado pasa a maintenanceRequired solo si no está ya en mantenimiento
        if (v.getStatus() != VehicleStatus.underMaintenance) {
            v.setStatus(VehicleStatus.maintenanceRequired);
            log.info("⚠️ Vehículo '{}' marcado como maintenanceRequired", v.getName());
        }
    }

    private void createMaintenanceRecordIfAbsent(Vehicle v, int cycleKm) {
        // Evitar duplicados por mismo km (chequeo simple en memoria)
        boolean exists = maintenanceRecordRepository
                .findByVehicleIdOrderByMaintenanceDateDesc(v.getId())
                .stream()
                .anyMatch(r -> Objects.equals(r.getKmAtMaintenance(), cycleKm));
        if (exists) {
            return;
        }

        MaintenanceRecord record = MaintenanceRecord.builder()
                .vehicle(v)
                .maintenanceDate(LocalDateTime.now())
                .kmAtMaintenance(cycleKm)
                .build();
        maintenanceRecordRepository.save(record);
        log.info("📝 Registro de mantenimiento creado (vehículo={}, km={})", v.getName(), cycleKm);
    }

    private boolean canApplyDirectStatus(VehicleStatus requested, VehicleStatus current) {
        // Permitir cambios solo si no interferimos con el flujo automático
        if (requested == VehicleStatus.outOfService) return true;
        if (requested == VehicleStatus.maintenanceCompleted
                && (current == VehicleStatus.maintenanceCompleted
                || current == VehicleStatus.underMaintenance)) return true;
        if (requested == VehicleStatus.underMaintenance
                && (current == VehicleStatus.maintenanceRequired
                || current == VehicleStatus.maintenanceCompleted)) return true;
        return false;
    }

    // =============================================
    // QUERIES
    // =============================================
    @Transactional(readOnly = true)
    public List<VehicleResponseDTO> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VehicleResponseDTO getVehicleById(UUID id) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle con id " + id + " no encontrado"));
        return toResponseDTO(v);
    }

    @Transactional(readOnly = true)
    public List<VehicleResponseDTO> getVehicleByType(VehicleTypeDTO typeDto) {
        String typeName = typeDto.getType();
        VehicleType type = vehicleTypeRepository.findByType(typeName)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle type '" + typeName + "' no encontrado"));
        return vehicleRepository.findByVehicleType(type).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehicleResponseDTO> getVehicleByCapacity(String capacity) {
        int cap = Integer.parseInt(capacity);
        return vehicleRepository.findByCapacity(cap).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehicleResponseDTO> getAvailableVehicles(Date startDate, Date endDate) {
        return vehicleRepository.findAvailableBetween(
                        VehicleStatus.maintenanceCompleted,
                        startDate,
                        endDate
                ).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // =============================================
    // MAPPERS
    // =============================================
    private MaintenanceRecordDTO toMaintenanceDTO(MaintenanceRecord r) {
        return MaintenanceRecordDTO.builder()
                .id(r.getId())
                .maintenanceDate(r.getMaintenanceDate())
                .kmAtMaintenance(r.getKmAtMaintenance())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }

    private VehicleResponseDTO toResponseDTO(Vehicle v) {
        List<MaintenanceRecordDTO> hist = maintenanceRecordRepository
                .findByVehicleIdOrderByMaintenanceDateDesc(v.getId())
                .stream()
                .map(this::toMaintenanceDTO)
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
                .mainImageBase64(v.getMainImageBase64())
                .listImagesBase64(v.getListImagesBase64())
                .insurancePhone(v.getInsurancePhone())
                .maintenanceRecords(hist)
                .build();
    }


    public void saveImages(UUID vehicleId, MultipartFile mainImage, List<MultipartFile> listImages) throws IOException, IOException {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        // Convert main image to Base64
        String mainImageBase64 = Base64.getEncoder().encodeToString(mainImage.getBytes());

        // Convert additional images to Base64
        List<String> listImagesBase64 = new ArrayList<>();
        for (MultipartFile img : listImages) {
            listImagesBase64.add(Base64.getEncoder().encodeToString(img.getBytes()));
        }

        vehicle.setMainImageBase64(mainImageBase64);
        vehicle.setListImagesBase64(listImagesBase64);

        vehicleRepository.save(vehicle);
    }

}
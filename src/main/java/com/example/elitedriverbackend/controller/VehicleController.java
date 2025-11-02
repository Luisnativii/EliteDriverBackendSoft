package com.example.elitedriverbackend.controller;

import com.example.elitedriverbackend.domain.dtos.CreateVehicleDTO;
import com.example.elitedriverbackend.domain.dtos.UpdateVehicleDTO;
import com.example.elitedriverbackend.domain.dtos.VehicleResponseDTO;
import com.example.elitedriverbackend.domain.dtos.VehicleTypeDTO;
import com.example.elitedriverbackend.services.VehicleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles")      // ← Aquí el prefijo /api
@Slf4j
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<Void> addVehicle(@RequestBody CreateVehicleDTO dto) {
        vehicleService.addVehicle(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateVehicle(
            @PathVariable String id,
            @RequestBody UpdateVehicleDTO dto) {

        vehicleService.updateVehicle(dto, UUID.fromString(id));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable String id) {
        vehicleService.deleteVehicle(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponseDTO>> getAllVehicles() {
        List<VehicleResponseDTO> list = vehicleService.getAllVehicles();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> getVehicleById(@PathVariable String id) {
        VehicleResponseDTO dto = vehicleService.getVehicleById(UUID.fromString(id));
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/by-type")
    public ResponseEntity<List<VehicleResponseDTO>> getByType(
            @RequestBody VehicleTypeDTO typeDto) {
        List<VehicleResponseDTO> list = vehicleService.getVehicleByType(typeDto);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/by-capacity")
    public ResponseEntity<List<VehicleResponseDTO>> getByCapacity(
            @RequestParam int capacity) {
        List<VehicleResponseDTO> list = vehicleService.getVehicleByCapacity(String.valueOf(capacity));
        return ResponseEntity.ok(list);
    }

    @GetMapping("/available")
    public ResponseEntity<List<VehicleResponseDTO>> getAvailable(
            @RequestParam Date startDate,
            @RequestParam Date endDate) {
        List<VehicleResponseDTO> list = vehicleService.getAvailableVehicles(startDate, endDate);
        return ResponseEntity.ok(list);
    }
}

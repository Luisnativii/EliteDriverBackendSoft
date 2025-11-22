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
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/*
    Controlador REST para gestionar vehículos.
    Proporciona endpoints para crear, actualizar, eliminar y consultar vehículos,
    así como para subir imágenes asociadas a un vehículo.
 */
@RestController
@RequestMapping("/api/vehicles")      // ← Aquí el prefijo /api
@Slf4j
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    /*
        Endpoint para agregar un nuevo vehículo.
        Recibe un objeto CreateVehicleDTO en el cuerpo de la solicitud.
     */
    @PostMapping
    public ResponseEntity<Void> addVehicle(@RequestBody CreateVehicleDTO dto) {
        vehicleService.addVehicle(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /*
        Endpoint para actualizar un vehículo existente.
        Recibe el ID del vehículo en la URL y un objeto UpdateVehicleDTO en el cuerpo de la solicitud.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateVehicle(
            @PathVariable String id,
            @RequestBody UpdateVehicleDTO dto) {

        vehicleService.updateVehicle(dto, UUID.fromString(id));
        return ResponseEntity.ok().build();
    }

    /*
        Endpoint para eliminar un vehículo por su ID.
        Recibe el ID del vehículo en la URL.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable String id) {
        vehicleService.deleteVehicle(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    /*
        Endpoint para obtener todos los vehículos.
        Retorna una lista de VehicleResponseDTO.
     */
    @GetMapping
    public ResponseEntity<List<VehicleResponseDTO>> getAllVehicles() {
        List<VehicleResponseDTO> list = vehicleService.getAllVehicles();
        return ResponseEntity.ok(list);
    }

    /*
        Endpoint para obtener un vehículo por su ID.
        Recibe el ID del vehículo en la URL y retorna un VehicleResponseDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> getVehicleById(@PathVariable String id) {
        VehicleResponseDTO dto = vehicleService.getVehicleById(UUID.fromString(id));
        return ResponseEntity.ok(dto);
    }

    /*
        Endpoint para obtener vehículos por tipo.
        Recibe un objeto VehicleTypeDTO en el cuerpo de la solicitud y retorna una lista de VehicleResponseDTO.
     */
    @PostMapping("/by-type")
    public ResponseEntity<List<VehicleResponseDTO>> getByType(
            @RequestBody VehicleTypeDTO typeDto) {
        List<VehicleResponseDTO> list = vehicleService.getVehicleByType(typeDto);
        return ResponseEntity.ok(list);
    }

    /*
        Endpoint para obtener vehículos por capacidad.
        Recibe un parámetro de consulta 'capacity' y retorna una lista de VehicleResponseDTO.
     */
    @GetMapping("/by-capacity")
    public ResponseEntity<List<VehicleResponseDTO>> getByCapacity(
            @RequestParam int capacity) {
        List<VehicleResponseDTO> list = vehicleService.getVehicleByCapacity(String.valueOf(capacity));
        return ResponseEntity.ok(list);
    }

    /*
        Endpoint para obtener vehículos disponibles en un rango de fechas.
        Recibe parámetros de consulta 'startDate' y 'endDate' y retorna una lista de VehicleResponseDTO.
     */
    @GetMapping("/available")
    public ResponseEntity<List<VehicleResponseDTO>> getAvailable(
            @RequestParam Date startDate,
            @RequestParam Date endDate) {
        List<VehicleResponseDTO> list = vehicleService.getAvailableVehicles(startDate, endDate);
        return ResponseEntity.ok(list);
    }

    /*
        Endpoint para subir imágenes asociadas a un vehículo.
        Recibe el ID del vehículo en la URL, una imagen principal y una lista de imágenes adicionales.
     */
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImages(
            @PathVariable String id,
            @RequestPart("mainImage") MultipartFile mainImage,
            @RequestPart("listImages") List<MultipartFile> listImages
    ) {
        try {
            vehicleService.saveImages(UUID.fromString(id), mainImage, listImages);
            return ResponseEntity.ok("Images uploaded successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading images: " + e.getMessage());
        }
    }

}

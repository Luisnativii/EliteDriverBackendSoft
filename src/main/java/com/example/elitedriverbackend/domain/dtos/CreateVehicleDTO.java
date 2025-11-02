package com.example.elitedriverbackend.domain.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateVehicleDTO {

    @NotBlank(message = "El nombre del vehículo es requerido")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @NotBlank(message = "La marca es requerida")
    @Size(min = 2, max = 50, message = "La marca debe tener entre 2 y 50 caracteres")
    private String brand;

    @NotBlank(message = "El modelo es requerido")
    @Size(min = 1, max = 50, message = "El modelo debe tener entre 1 y 50 caracteres")
    private String model;

    @NotNull(message = "La capacidad es requerida")
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    @Max(value = 50, message = "La capacidad no puede ser mayor a 50")
    private Integer capacity;

    @NotNull(message = "El precio por día es requerido")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "Formato de precio inválido")
    private BigDecimal pricePerDay;

    @NotNull(message = "Los kilómetros son requeridos")
    @Min(value = 0, message = "Los kilómetros no pueden ser negativos")
    private Integer kilometers;

    // Lista de características del vehículo (opcional)
    private List<String> features;

    // Estructura anidada para el tipo de vehículo
    @NotNull(message = "El tipo de vehículo es requerido")
    @Valid
    private VehicleTypeInfo vehicleType;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VehicleTypeInfo {
        @NotBlank(message = "El tipo de vehículo es requerido")
        private String type;
    }

    @NotNull
    private Integer kmForMaintenance;

    private String mainImageUrl;
    private List<String> imageUrls;

    private String insurancePhone;

}
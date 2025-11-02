package com.example.elitedriverbackend.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleTypeDTO {

    @NotBlank(message = "El tipo de vehículo no puede estar vacío")
    private String type;
}
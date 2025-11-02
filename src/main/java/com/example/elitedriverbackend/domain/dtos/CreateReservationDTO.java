package com.example.elitedriverbackend.domain.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateReservationDTO {

    @NotNull(message = "La fecha de inicio es requerida")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate startDate;

    @NotNull(message = "La fecha de fin es requerida")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate endDate;

    @NotBlank(message = "El ID del usuario es requerido")
    private String userId;

    @NotBlank(message = "El ID del vehículo es requerido")
    private String vehicleId;
}

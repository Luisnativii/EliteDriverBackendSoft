package com.example.elitedriverbackend.domain.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StartPaymentReservationDTO {
    private String userId;
    private String vehicleId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long amountCents;
    private String currency; // USD
}
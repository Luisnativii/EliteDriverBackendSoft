package com.example.elitedriverbackend.domain.dtos;

import com.example.elitedriverbackend.domain.entity.VehicleStatus;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleResponseDTO {
    private String id;
    private String name;
    private String brand;
    private String model;
    private Integer capacity;
    private BigDecimal pricePerDay;
    private Integer kilometers;
    private List<String> features;
    private VehicleTypeInfo vehicleType;
    private Integer kmForMaintenance;
    private VehicleStatus status;
    private String mainImageBase64;
    private List<String> listImagesBase64;
    private String insurancePhone;

    private List<MaintenanceRecordDTO> maintenanceRecords;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VehicleTypeInfo {
        private String id;
        private String type;
    }
}

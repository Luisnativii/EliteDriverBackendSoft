package com.example.elitedriverbackend.domain.dtos;

import lombok.*;

import java.util.Date;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponseDTO {
    private String id;
    private Date startDate;
    private Date endDate;
    private String status;
    private Double totalPrice;

    private UserInfo user;

    private VehicleInfo vehicle;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfo {
        private String id;
        private String firstName;
        private String lastName;
        private String email;
        private String dui;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VehicleInfo {
        private String id;
        private String name;
        private String brand;
        private String model;
        private Integer capacity;
        private String mainImageBase64;
        private double pricePerDay;
        private String vehicleType;

    }
}

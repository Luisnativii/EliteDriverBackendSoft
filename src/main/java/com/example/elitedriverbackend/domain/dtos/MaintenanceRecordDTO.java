package com.example.elitedriverbackend.domain.dtos;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaintenanceRecordDTO {
    private UUID id;
    private LocalDateTime maintenanceDate;
    private Integer kmAtMaintenance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}




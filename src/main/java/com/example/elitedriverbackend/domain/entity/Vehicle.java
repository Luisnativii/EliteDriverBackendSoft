package com.example.elitedriverbackend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"vehicleType", "maintenanceRecords"})
@ToString(exclude = {"vehicleType", "maintenanceRecords"})
public class Vehicle {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private BigDecimal pricePerDay;

    @Column(nullable = false)
    private Integer kilometers;

    /**
     * Número de teléfono de la aseguradora (opcional).
     * Nota: no ponemos nullable = false, queda nullable = true por defecto.
     */
    @Column(name = "insurance_phone")
    private String insurancePhone;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "vehicle_features",
            joinColumns = @JoinColumn(name = "vehicle_id")
    )
    @Column(name = "feature", nullable = false)
    private List<String> features = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_type_id", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "km_for_maintenance")
    private Integer kmForMaintenance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    @Column(name = "main_image_url")
    private String mainImageUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "vehicle_images",
            joinColumns = @JoinColumn(name = "vehicle_id")
    )
    @Column(name = "image_url", nullable = false)
    private List<String> imageUrls = new ArrayList<>();

    @OneToMany(
            mappedBy = "vehicle",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<MaintenanceRecord> maintenanceRecords = new HashSet<>();


    // =============================================
    // MÉTODOS DE LÓGICA DE MANTENIMIENTO
    // =============================================

    public Integer getNextMaintenanceKm() {
        if (kilometers == null || kmForMaintenance == null) return null;
        int cycles = (int) Math.ceil((double) kilometers / kmForMaintenance);
        return cycles * kmForMaintenance;
    }

    public Integer getKmUntilMaintenance() {
        Integer next = getNextMaintenanceKm();
        if (next == null || kilometers == null) return null;
        return Math.max(0, next - kilometers);
    }

    public boolean needsMaintenance() {
        if (kilometers == null || kmForMaintenance == null) return false;
        return kilometers > 0 && kilometers % kmForMaintenance == 0;
    }

    public Integer getCompletedMaintenanceCycles() {
        if (kilometers == null || kmForMaintenance == null) return 0;
        return kilometers / kmForMaintenance;
    }
}

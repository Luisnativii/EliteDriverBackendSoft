package com.example.elitedriverbackend.repositories;

import com.example.elitedriverbackend.domain.entity.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface VehicleTypeRepository extends JpaRepository<VehicleType, UUID> {

    Optional<VehicleType> findByType(String type);
}

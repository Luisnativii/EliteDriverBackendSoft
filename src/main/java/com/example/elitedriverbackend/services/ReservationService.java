package com.example.elitedriverbackend.services;

import com.example.elitedriverbackend.domain.dtos.CreateReservationDTO;
import com.example.elitedriverbackend.domain.entity.Reservation;
import com.example.elitedriverbackend.domain.entity.User;
import com.example.elitedriverbackend.domain.entity.Vehicle;
import com.example.elitedriverbackend.domain.entity.VehicleType;
import com.example.elitedriverbackend.repositories.ReservationRepository;
import com.example.elitedriverbackend.repositories.UserRepository;
import com.example.elitedriverbackend.repositories.VehicleRepository;
import com.example.elitedriverbackend.repositories.VehicleTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;

    public Reservation addReservation(CreateReservationDTO createReservationDTO) {

        User user = userRepository.findById(UUID.fromString(createReservationDTO.getUserId()))
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Vehicle vehicle = vehicleRepository.findById(UUID.fromString(createReservationDTO.getVehicleId()))
                .orElseThrow(() -> new EntityNotFoundException("Vehiculo no encontrado"));
        // Convertir LocalDate a java.sql.Date (sin hora ni desfase)
        LocalDate start = createReservationDTO.getStartDate();
        LocalDate end = createReservationDTO.getEndDate();

        Date startDate = java.sql.Date.valueOf(start);
        Date endDate = java.sql.Date.valueOf(end);



        // Validar si ya está reservado en ese rango
        List<Reservation> overlappingReservations = reservationRepository
                .findByVehicleIdAndDateOverlap(vehicle.getId(), startDate, endDate);

        if (!overlappingReservations.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "❌ Este vehículo ya está reservado en el rango de fechas seleccionado"
            );
              }


        Reservation newReservation = new Reservation();
        newReservation.setStartDate(startDate);
        newReservation.setEndDate(endDate);
        newReservation.setUser(user);
        newReservation.setVehicle(vehicle);
        return reservationRepository.save(newReservation);
    }

    public void deleteReservation(UUID id) {
        if (!reservationRepository.existsById(id)) {
            throw new EntityNotFoundException("Reserva con id " + id + " no encontrada");
        }
        reservationRepository.deleteById(id);
    }

    public List<Reservation> getAllReservations() {
            log.info("Obteniendo todas las reservas");
            List<Reservation> reservations = reservationRepository.findAll();

            log.info("Total de Reservas encontradas: {}", reservations.size());

            for (Reservation r : reservations) {
                try{
                    log.info("Reserva ID: {}", r.getId());
                    log.info("Usuario: {}", r.getUser().getFirstName() + " " + r.getUser().getLastName());
                    log.info("Vehículo: {}", r.getVehicle().getName());
                    log.info("Tipo de vehículo: {}", r.getVehicle().getVehicleType() != null ? r.getVehicle().getVehicleType().getType() : "N/A");
                    log.info("DUI del usuario: {}", r.getUser().getDui());
                    log.info("Correo del usuario: {}", r.getUser().getEmail());
                    log.info("Fecha de inicio: {}", r.getStartDate());
                    log.info("Fecha de fin: {}", r.getEndDate());
                } catch (Exception innerEx) {
                    log.error("❌ Error procesando reserva con ID: {}", r.getId(), innerEx);
                }
            }
            return reservations;
    }

    public Reservation getReservationById(UUID id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva con id " + id + " no encontrada"));
    }
    // Metodos que faltan
    // GetByDateRange, GetByUser, GetByTypeOfVehicle, GetByVehicle

    public List<Reservation> getReservationByRange(Date startDate, Date endDate) {
        return reservationRepository.findByStartDateBetween(startDate, endDate);
    }
    public List<Reservation> getReservationByUser(UUID user) {
            return reservationRepository.findAll().stream()
                    .filter(reservation -> reservation.getUser().getId().equals(user))
                    .toList();
    }

    public List<Reservation> getReservationByVehicle(UUID vehicle) {
            return reservationRepository.findAll().stream()
                    .filter(reservation -> reservation.getVehicle().getId().equals(vehicle))
                    .toList();
    }

    public List<Reservation> getReservationByVehicleType(String vehicleType) {
            VehicleType type = vehicleTypeRepository.findByType(vehicleType)
                    .orElseThrow(() -> new EntityNotFoundException("Tipo de vehículo no encontrado"));
            return reservationRepository.findByVehicle_VehicleType(type);
    }
}

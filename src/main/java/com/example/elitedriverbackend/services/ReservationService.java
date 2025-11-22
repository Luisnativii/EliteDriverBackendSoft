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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/*
    Servicio para manejar la lógica de negocio relacionada con las reservas de vehículos.
    Proporciona métodos para crear, eliminar y consultar reservas.
 */
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

    /*
        Crea una nueva reserva si el vehículo no está ya reservado en el rango de fechas especificado.
        Recibe un DTO con los datos necesarios para crear la reserva.
        Retorna la reserva creada.
        Si el vehículo ya está reservado en ese rango, lanza una excepción.
     */
    public Reservation addReservation(CreateReservationDTO createReservationDTO) {

        User user = userRepository.findById(UUID.fromString(createReservationDTO.getUserId()))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Vehicle vehicle = vehicleRepository.findById(UUID.fromString(createReservationDTO.getVehicleId()))
                .orElseThrow(() -> new RuntimeException("Vehiculo no encontrado"));
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

    /*
        Elimina una reserva por su ID.
        Si la reserva no existe, lanza una excepción.
     */
    public void deleteReservation(UUID id) {
        if (!reservationRepository.existsById(id)) {
            throw new RuntimeException("Reserva con id " + id + " no encontrada");
        }
        reservationRepository.deleteById(id);
    }

    /*
        Obtiene todas las reservas y las registra en el log.
        Retorna la lista de reservas.
     */
    public List<Reservation> getAllReservations() {
        try{
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
        } catch (Exception e){
            log.error("❌ Error obteniendo reservas: ", e);
            throw new RuntimeException("Error obteniendo reservas: " + e.getMessage(), e);

        }
    }

    /*
        Obtiene una reserva por su ID.
        Si la reserva no existe, lanza una excepción.
     */
    public Reservation getReservationById(UUID id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva con id " + id + " no encontrada"));
    }


    /*
        Obtiene las reservas que caen dentro de un rango de fechas especificado.
        Retorna la lista de reservas en ese rango.
     */
    public List<Reservation> getReservationByRange(Date startDate, Date endDate) {
        try {
            return reservationRepository.findByStartDateBetween(startDate, endDate);
        }catch (Exception e){
            throw new RuntimeException("Error obteniendo reservas: " + e.getMessage(), e);
        }
    }

    /*
        Obtiene las reservas asociadas a un usuario específico por su ID.
        Retorna la lista de reservas del usuario.
     */
    public List<Reservation> getReservationByUser(UUID user) {
        try {
            return reservationRepository.findAll().stream()
                    .filter(reservation -> reservation.getUser().getId().equals(user))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo reservas por usuario: " + e.getMessage(), e);
        }
    }

    /*
        Obtiene las reservas asociadas a un vehículo específico por su ID.
        Retorna la lista de reservas del vehículo.
     */
    public List<Reservation> getReservationByVehicle(UUID vehicle) {
        try {
            return reservationRepository.findAll().stream()
                    .filter(reservation -> reservation.getVehicle().getId().equals(vehicle))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo reservas por vehículo: " + e.getMessage(), e);
        }
    }

    /*
        Obtiene las reservas asociadas a un tipo de vehículo específico.
        Retorna la lista de reservas del tipo de vehículo.
     */
    public List<Reservation> getReservationByVehicleType(String vehicleType) {
        try {
            VehicleType type = vehicleTypeRepository.findByType(vehicleType)
                    .orElseThrow(() -> new RuntimeException("Tipo de vehículo no encontrado"));
            return reservationRepository.findByVehicle_VehicleType(type);
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo reservas por tipo de vehículo: " + e.getMessage(), e);
        }
    }
}
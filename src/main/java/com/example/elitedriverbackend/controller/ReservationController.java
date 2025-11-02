package com.example.elitedriverbackend.controller;

import com.example.elitedriverbackend.domain.dtos.CreateReservationDTO;
import com.example.elitedriverbackend.domain.dtos.ReservationResponseDTO;
import com.example.elitedriverbackend.domain.entity.Reservation;
import com.example.elitedriverbackend.services.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponseDTO> addReservation(@Valid @RequestBody CreateReservationDTO dto) {
        Reservation reservation = reservationService.addReservation(dto);
        ReservationResponseDTO responseDTO = convertToDTO(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }


    @GetMapping("{id}")
    public ResponseEntity<Reservation> getReservation(@PathVariable String id) {
        UUID uuid = parseUUID(id);
        Reservation reservation = reservationService.getReservationById(uuid);
        return new ResponseEntity<>(reservation, HttpStatus.OK);

    }

    private UUID parseUUID(String id) {
        try{
            return UUID.fromString(id);
        }catch(IllegalArgumentException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id no es un UUID válido: " + id);
        }
    }




    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations() {
        try{
            List<Reservation> reservations = reservationService.getAllReservations();

            List<ReservationResponseDTO> reservationDTOs = reservations.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(reservationDTOs);
        }catch (Exception e){
            log.error("Error en getAllReservations: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error en getAllReservations: " + e.getMessage());
        }
    }

    private ReservationResponseDTO convertToDTO(Reservation reservation) {
        if (reservation.getVehicle() == null) {
            throw new RuntimeException("Reservation con id " + reservation.getId() + " no tiene vehículo asociado");
        }

        // ✅ Calcular totalPrice
        long diffMillis = reservation.getEndDate().getTime() - reservation.getStartDate().getTime();
        int days = (int) Math.ceil(diffMillis / (1000.0 * 60 * 60 * 24));
        double pricePerDay = reservation.getVehicle().getPricePerDay().doubleValue();
        double totalPrice = days * pricePerDay;

        return ReservationResponseDTO.builder()
                .id(String.valueOf(reservation.getId()))
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .status("confirmado")
                .totalPrice(totalPrice) // ✅ Usar el cálculo
                .user(ReservationResponseDTO.UserInfo.builder()
                        .id(String.valueOf(reservation.getUser().getId()))
                        .firstName(reservation.getUser().getFirstName())
                        .lastName(reservation.getUser().getLastName())
                        .email(reservation.getUser().getEmail())
                        .dui(reservation.getUser().getDui())
                        .build())
                .vehicle(ReservationResponseDTO.VehicleInfo.builder()
                        .id(String.valueOf(reservation.getVehicle().getId()))
                        .name(reservation.getVehicle().getName())
                        .brand(reservation.getVehicle().getBrand())
                        .model(reservation.getVehicle().getModel())
                        .capacity(reservation.getVehicle().getCapacity())
                        .mainImageUrl(reservation.getVehicle().getMainImageUrl())
                        .vehicleType(reservation.getVehicle().getVehicleType().getType())
                        .pricePerDay(pricePerDay)
                        .build())
                .build();
    }


    @GetMapping("/date")
    public ResponseEntity<List<ReservationResponseDTO>> getReservationByRange(@RequestParam("startDate") String startDateStr,
                                                                    @RequestParam("endDate") String endDateStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);

            if(startDate.after(endDate)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de inicio no puede ser posterior a la fecha de fin");
            }
            List<Reservation> reservations = reservationService.getReservationByRange(startDate, endDate);
            List<ReservationResponseDTO> dtos = reservations.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (ParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fecha no válida: " + e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<ReservationResponseDTO>> getReservationByUser(@RequestParam("userId") String userId) {
        try {
            UUID uuid = UUID.fromString(userId); // conversión segura con try-catch

            List<Reservation> reservations = reservationService.getReservationByUser(uuid);

            List<ReservationResponseDTO> reservationDTOs = reservations.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(reservationDTOs);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UUID inválido: " + userId);
        }
    }



    @GetMapping("/vehicle")
    public ResponseEntity<List<Reservation>> getReservationByVehicle(@RequestParam("vehicleId") String vehicleId) {
        try{
            UUID uuid = parseUUID(vehicleId);
            List<Reservation> reservations = reservationService.getReservationByVehicle(uuid);
            return ResponseEntity.ok(reservations);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error en getReservationByVehicle: " + e.getMessage());
        }
    }

    @GetMapping("/vehicleType")
    public ResponseEntity<List<Reservation>> getReservationByVehicleType(@RequestParam("vehicleType") String vehicleType) {
        try {
            List<Reservation> reservations = reservationService.getReservationByVehicleType(vehicleType);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error en getReservationByVehicleType: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable String id) {
        UUID uuid = parseUUID(id);
        reservationService.deleteReservation(uuid);
        return ResponseEntity.ok().build();
    }

}

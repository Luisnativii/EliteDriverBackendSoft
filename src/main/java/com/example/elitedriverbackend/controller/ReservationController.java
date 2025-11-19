package com.example.elitedriverbackend.controller;

import com.example.elitedriverbackend.domain.dtos.CreateReservationDTO;
import com.example.elitedriverbackend.domain.dtos.ReservationResponseDTO;
import com.example.elitedriverbackend.domain.entity.Reservation;
import com.example.elitedriverbackend.services.ReservationService;
import jakarta.persistence.EntityNotFoundException;
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
        return UUID.fromString(id);
    }




    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations() {
            List<Reservation> reservations = reservationService.getAllReservations();

            List<ReservationResponseDTO> reservationDTOs = reservations.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(reservationDTOs);
    }

    private ReservationResponseDTO convertToDTO(Reservation reservation) {
        if (reservation.getVehicle() == null) {
            throw new EntityNotFoundException("Reservation con id " + reservation.getId() + " no tiene vehículo asociado");
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
                        .mainImageBase64(reservation.getVehicle().getMainImageBase64())
                        .vehicleType(reservation.getVehicle().getVehicleType().getType())
                        .pricePerDay(pricePerDay)
                        .build())
                .build();
    }


    @GetMapping("/date")
    public ResponseEntity<List<ReservationResponseDTO>> getReservationByRange(@RequestParam("startDate") String startDateStr,
                                                                    @RequestParam("endDate") String endDateStr) throws ParseException {

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

    }

    @GetMapping("/user")
    public ResponseEntity<List<ReservationResponseDTO>> getReservationByUser(@RequestParam("userId") String userId) {
            UUID uuid = parseUUID(userId);

            List<Reservation> reservations = reservationService.getReservationByUser(uuid);

            List<ReservationResponseDTO> reservationDTOs = reservations.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(reservationDTOs);
    }



    @GetMapping("/vehicle")
    public ResponseEntity<List<Reservation>> getReservationByVehicle(@RequestParam("vehicleId") String vehicleId) {
            UUID uuid = parseUUID(vehicleId);
            List<Reservation> reservations = reservationService.getReservationByVehicle(uuid);
            return ResponseEntity.ok(reservations);
    }

    @GetMapping("/vehicleType")
    public ResponseEntity<List<Reservation>> getReservationByVehicleType(@RequestParam("vehicleType") String vehicleType) {
            List<Reservation> reservations = reservationService.getReservationByVehicleType(vehicleType);
            return ResponseEntity.ok(reservations);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable String id) {
        UUID uuid = parseUUID(id);
        reservationService.deleteReservation(uuid);
        return ResponseEntity.ok().build();
    }

}

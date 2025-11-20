package com.example.elitedriverbackend.services;

import com.example.elitedriverbackend.domain.dtos.StartPaymentReservationDTO;
import com.example.elitedriverbackend.domain.entity.Reservation;
import com.example.elitedriverbackend.domain.entity.ReservationStatus;
import com.example.elitedriverbackend.domain.entity.User;
import com.example.elitedriverbackend.domain.entity.Vehicle;
import com.example.elitedriverbackend.repositories.ReservationRepository;
import com.example.elitedriverbackend.repositories.UserRepository;
import com.example.elitedriverbackend.repositories.VehicleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    @Value("${wompi.public-key}")
    private String wompiPublicKey;
    @Value("${wompi.private-key}")
    private String wompiPrivateKey;
    @Value("${wompi.api-base}")
    private String wompiApiBase;
    @Value("${wompi.reservation-expiration-minutes:15}")
    private long expirationMinutes;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Reservation startPayment(StartPaymentReservationDTO dto) {
        User user = userRepository.findById(UUID.fromString(dto.getUserId()))
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        Vehicle vehicle = vehicleRepository.findById(UUID.fromString(dto.getVehicleId()))
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado"));

        LocalDate start = dto.getStartDate();
        LocalDate end = dto.getEndDate();
        if (end.isBefore(start)) throw new IllegalArgumentException("Rango de fechas inválido");

        Date startDate = java.sql.Date.valueOf(start);
        Date endDate = java.sql.Date.valueOf(end);

        var overlaps = reservationRepository.findConfirmedOverlap(vehicle.getId(), startDate, endDate);
        if (!overlaps.isEmpty()) throw new IllegalStateException("Vehículo ya reservado en ese rango");

        String paymentReference = UUID.randomUUID().toString();

        Reservation reservation = Reservation.builder()
                .startDate(startDate)
                .endDate(endDate)
                .user(user)
                .vehicle(vehicle)
                .status(ReservationStatus.PENDING_PAYMENT)
                .paymentReference(paymentReference)
                .amountCents(dto.getAmountCents())
                .currency(dto.getCurrency() != null ? dto.getCurrency() : "USD")
                .expiresAt(Instant.now().plusSeconds(expirationMinutes * 60))
                .build();

        reservationRepository.save(reservation);

        Map<String, Object> payload = new HashMap<>();
        payload.put("amount_in_cents", dto.getAmountCents());
        payload.put("currency", reservation.getCurrency());
        payload.put("customer_email", user.getEmail());
        payload.put("reference", paymentReference);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(wompiPrivateKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    wompiApiBase + "/transactions",
                    entity,
                    String.class
            );
            log.info("Wompi respuesta: {}", response.getBody());
            extractTransactionIdIfPresent(reservation, response.getBody());
        } catch (Exception e) {
            log.error("Error creando transacción Wompi: {}", e.getMessage());
            reservation.setStatus(ReservationStatus.FAILED);
            reservation.setFailureReason("WOMPI_INIT_ERROR");
            reservationRepository.save(reservation);
        }

        return reservation;
    }

    private void extractTransactionIdIfPresent(Reservation reservation, String body) {
        if (body == null) return;
        try {
            JsonNode root = objectMapper.readTree(body);
            JsonNode data = root.get("data");
            if (data != null && data.get("id") != null) {
                reservation.setWompiTransactionId(data.get("id").asText());
                reservationRepository.save(reservation);
            }
        } catch (Exception e) {
            log.debug("No se pudo extraer id de transacción: {}", e.getMessage());
        }
    }

    public Reservation markConfirmed(String paymentReference, String wompiTransactionId, String methodType) {
        Reservation reservation = reservationRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada para referencia"));

        if (reservation.getStatus() == ReservationStatus.CONFIRMED) return reservation;

        if (reservation.isExpiredPending()) {
            reservation.setStatus(ReservationStatus.EXPIRED);
            reservationRepository.save(reservation);
            throw new IllegalStateException("Reserva expirada");
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setWompiTransactionId(wompiTransactionId);
        reservation.setPaymentMethodType(methodType);
        reservationRepository.save(reservation);
        return reservation;
    }

    public Reservation markFailed(String paymentReference, String reason) {
        Reservation reservation = reservationRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada"));
        if (reservation.getStatus() == ReservationStatus.CONFIRMED ||
                reservation.getStatus() == ReservationStatus.EXPIRED) return reservation;
        reservation.setStatus(ReservationStatus.FAILED);
        reservation.setFailureReason(reason);
        reservationRepository.save(reservation);
        return reservation;
    }

    public void expirePendings() {
        reservationRepository.findExpiredPendings().forEach(r -> {
            if (r.getStatus() == ReservationStatus.PENDING_PAYMENT) {
                r.setStatus(ReservationStatus.EXPIRED);
                reservationRepository.save(r);
                log.info("Reserva expirada (reference={} id={})", r.getPaymentReference(), r.getId());
            }
        });
    }
}
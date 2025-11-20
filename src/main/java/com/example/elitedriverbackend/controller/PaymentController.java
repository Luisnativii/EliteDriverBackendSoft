package com.example.elitedriverbackend.controllers;

import com.example.elitedriverbackend.domain.dtos.StartPaymentReservationDTO;
import com.example.elitedriverbackend.domain.entity.Reservation;
import com.example.elitedriverbackend.services.PaymentReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentReservationService paymentService;

    @PostMapping("/reservations")
    public ResponseEntity<Reservation> start(@RequestBody StartPaymentReservationDTO dto) {
        return ResponseEntity.ok(paymentService.startPayment(dto));
    }
}
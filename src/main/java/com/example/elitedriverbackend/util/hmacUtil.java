// ReservationExpirationScheduler.java
package com.example.elitedriverbackend.util;

import com.example.elitedriverbackend.services.PaymentReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ReservationExpirationScheduler {

    private final PaymentReservationService service;

    @Scheduled(fixedDelay = 300000) // cada 5 min
    public void expirePendings() {
        service.expirePendings();
    }
}
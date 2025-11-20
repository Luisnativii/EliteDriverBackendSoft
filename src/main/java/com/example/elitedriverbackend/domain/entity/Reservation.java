package com.example.elitedriverbackend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reservations")
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue
    private UUID id;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Vehicle vehicle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    // Datos de pago Wompi
    private String wompiTransactionId;
    private String paymentReference;      // Referencia propia interna
    private Long amountCents;             // Monto en centavos
    private String currency;              // "USD"
    private String paymentMethodType;     // card, bank_transfer, etc.

    // Auditoría y expiración
    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    private Instant expiresAt;            // Para PENDING_PAYMENT

    private String failureReason;

    public boolean isExpiredPending() {
        return status == ReservationStatus.PENDING_PAYMENT
                && expiresAt != null
                && Instant.now().isAfter(expiresAt);
    }
}
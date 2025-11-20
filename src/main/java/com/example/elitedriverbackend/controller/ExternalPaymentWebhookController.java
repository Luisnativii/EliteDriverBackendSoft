package com.example.elitedriverbackend.controllers;

import com.example.elitedriverbackend.services.PaymentReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks/external")
@RequiredArgsConstructor
@Slf4j
public class ExternalPaymentWebhookController {

    private final PaymentReservationService paymentService;

    @PostMapping
    public ResponseEntity<String> handle(@RequestBody Map<String, Object> body,
                                         @RequestHeader(value = "X-Signature", required = false) String signature) {

        // 1. (Opcional) verificar firma si el proveedor la envía
        // if (!verifySignature(signature, body)) return ResponseEntity.status(401).body("firma inválida");

        String rawStatus = str(body.get("ResultadoTransaccion"));
        String transactionId = str(body.get("IdTransaccion"));

        // Posible referencia asociada a tu reserva:
        String paymentReference = null;
        Object enlacePago = body.get("EnlacePago");
        if (enlacePago instanceof Map<?, ?> enlaceMap) {
            paymentReference = str(enlaceMap.get("IdentificadorEnlaceComercio"));
        }

        if (paymentReference == null) {
            log.warn("Webhook sin referencia; se ignora");
            return ResponseEntity.ok("sin referencia");
        }

        String normalized = normalizeStatus(rawStatus);
        try {
            switch (normalized) {
                case "APPROVED":
                    paymentService.markConfirmed(paymentReference, transactionId, "EXTERNAL");
                    break;
                case "DECLINED":
                case "ERROR":
                    paymentService.markFailed(paymentReference, normalized);
                    break;
                default:
                    log.info("Estado ignorado: {}", rawStatus);
            }
        } catch (Exception e) {
            log.error("Error procesando webhook externo: {}", e.getMessage());
        }
        return ResponseEntity.ok("ok");
    }

    private String normalizeStatus(String s) {
        if (s == null) return "UNKNOWN";
        String v = s.toLowerCase();
        if (v.contains("exitosa") || v.contains("aprobada")) return "APPROVED";
        if (v.contains("fallida") || v.contains("declinada") || v.contains("rechazada")) return "DECLINED";
        if (v.contains("error") || v.contains("cancel")) return "ERROR";
        return "UNKNOWN";
    }

    private String str(Object o) {
        return o == null ? null : o.toString();
    }

    // Stub de firma (rellenar según proveedor)
    /*
    private boolean verifySignature(String signature, Map<String,Object> body) {
        return true;
    }
    */
}
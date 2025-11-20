// WompiWebhookController.java
package com.example.elitedriverbackend.controllers;

import com.example.elitedriverbackend.services.PaymentReservationService;
import com.example.elitedriverbackend.util.HmacUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks/wompi")
@RequiredArgsConstructor
@Slf4j
public class WompiWebhookController {

    @Value("${wompi.webhook-secret}")
    private String webhookSecret;

    private final PaymentReservationService paymentService;

    @PostMapping
    public ResponseEntity<String> handle(@RequestBody Map<String, Object> body,
                                         @RequestHeader("X-Wompi-Signature") String signature) {

        String payloadJson = toJson(body);
        if (!HmacUtil.verifySha256(webhookSecret, payloadJson, signature)) {
            return ResponseEntity.status(401).body("firma inválida");
        }

        Map<String, Object> data = cast(body.get("data"));
        Map<String, Object> tx   = data != null ? cast(data.get("transaction")) : null;
        if (tx == null) return ResponseEntity.ok("sin transaccion");

        String reference   = (String) tx.get("reference");
        String wompiId     = (String) tx.get("id");
        String status      = (String) tx.get("status");
        String methodType  = (String) tx.get("payment_method_type");

        try {
            switch (status) {
                case "APPROVED":
                    paymentService.markConfirmed(reference, wompiId, methodType);
                    break;
                case "DECLINED":
                case "ERROR":
                    paymentService.markFailed(reference, status);
                    break;
                default:
                    log.info("Estado ignorado {}", status);
            }
        } catch (Exception e) {
            log.error("Error procesando webhook: {}", e.getMessage());
        }
        return ResponseEntity.ok("ok");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> cast(Object o) {
        return (o instanceof Map) ? (Map<String, Object>) o : null;
    }

    private String toJson(Map<String, Object> map) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(map);
        } catch (Exception e) {
            return "{}";
        }
    }
}
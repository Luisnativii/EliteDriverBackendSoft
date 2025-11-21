package com.example.elitedriverbackend.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

@Component
public class WompiOAuthClient {

    @Value("${wompi.api-base}")
    private String baseUrl;
    @Value("${wompi.client-id}")
    private String clientId;
    @Value("${wompi.client-secret}")
    private String clientSecret;

    private final RestTemplate rt = new RestTemplate();

    private String cachedToken;
    private Instant expiresAt;

    public synchronized String getToken() {
        if (cachedToken != null && expiresAt != null && Instant.now().isBefore(expiresAt.minusSeconds(30))) {
            return cachedToken;
        }
        return fetchToken();
    }

    private String fetchToken() {
        String url = baseUrl + "/oauth/token";
        Map<String, String> body = Map.of(
                "client_id", clientId,
                "client_secret", clientSecret,
                "grant_type", "client_credentials"
        );
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Map> resp = rt.postForEntity(url, new HttpEntity<>(body, h), Map.class);

        if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
            Object access = resp.getBody().get("access_token");
            Object expires = resp.getBody().get("expires_in");
            if (access instanceof String token) {
                cachedToken = token;
                long seconds = (expires instanceof Number n) ? n.longValue() : 300;
                expiresAt = Instant.now().plusSeconds(seconds);
                return token;
            }
        }
        return null;
    }
}
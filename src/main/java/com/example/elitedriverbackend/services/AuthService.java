package com.example.elitedriverbackend.services;

import com.example.elitedriverbackend.domain.dtos.AuthRequest;
import com.example.elitedriverbackend.domain.dtos.AuthResponse;
import com.example.elitedriverbackend.domain.dtos.RegisterRequest;
import com.example.elitedriverbackend.domain.dtos.UserResponse;
import com.example.elitedriverbackend.domain.entity.User;
import com.example.elitedriverbackend.repositories.UserRepository;
import com.example.elitedriverbackend.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public String register(RegisterRequest request) {
        // Verificar si el usuario ya existe
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear nuevo usuario
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .birthDate(request.getBirthDate())
                .dui(request.getDui())
                .phoneNumber(request.getPhoneNumber())
                .role("CUSTOMER") // Por defecto
                .build();

        userRepository.save(user);
        return "Usuario registrado exitosamente";
    }

    public AuthResponse login(AuthRequest request) {
        log.info("Buscando usuario con email: {}", request.getEmail());

        // Buscar usuario por email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado: {}", request.getEmail());
                    return new RuntimeException("Credenciales inválidas");
                });

        log.info("Usuario encontrado: {}", user.getEmail());

        // Verificar contraseña usando AuthenticationManager
        try {
            var auth = new UsernamePasswordAuthenticationToken(
                    request.getEmail(), request.getPassword()
            );
            authManager.authenticate(auth);
        } catch (Exception e) {
            log.warn("Contraseña incorrecta para: {}", request.getEmail());
            throw new RuntimeException("Credenciales inválidas");
        }

        // Generar token JWT
        String token = jwtService.generateToken(user.getEmail());
        log.info("Token generado para: {}", user.getEmail());

        // Crear respuesta con UUID convertido a String si es necesario
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .phoneNumber(user.getPhoneNumber())
                .dui(user.getDui())
                .birthDate(LocalDate.parse(user.getBirthDate()))
                .build();

        return AuthResponse.builder()
                .token(token)
                .user(userResponse)
                .message("Login exitoso")
                .build();
    }

    public boolean validateToken(String token) {
        try {
            return jwtService.isTokenValid(token);
        } catch (Exception e) {
            log.error("Error validando token: {}", e.getMessage());
            return false;
        }
    }
}
package com.unimag.emitix.service;

import com.unimag.emitix.dto.LoginRequest;
import com.unimag.emitix.dto.LoginResponse;
import com.unimag.emitix.dto.RegisterRequest;
import com.unimag.emitix.entity.Role;
import com.unimag.emitix.entity.User;
import com.unimag.emitix.repository.UserRepository;
import com.unimag.emitix.security.JwtTokenProvider;
import com.unimag.emitix.security.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(user);

        log.info("User '{}' logged in successfully", user.getUsername());

        return new LoginResponse(token, user.getUsername(), user.getFullName(), user.getRole().name());
    }

    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        tokenBlacklistService.blacklist(token);
        log.info("Token invalidated successfully (logout)");
    }

    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("El username '" + request.username() + "' ya está en uso");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El email '" + request.email() + "' ya está registrado");
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .fullName(request.fullName())
                .phone(request.phone())
                .role(Role.ACCOUNTANT)   // rol por defecto al registrarse
                .isActive(true)
                .build();

        userRepository.save(user);
        log.info("User '{}' registered successfully", user.getUsername());

        String token = jwtTokenProvider.generateToken(user);
        return new LoginResponse(token, user.getUsername(), user.getFullName(), user.getRole().name());
    }
}

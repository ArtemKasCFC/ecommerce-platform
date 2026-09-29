package com.petproject.ecommerce.auth.controller;

import com.petproject.ecommerce.auth.dto.request.LoginRequest;
import com.petproject.ecommerce.auth.dto.response.LoginResponse;
import com.petproject.ecommerce.auth.service.AuthService;
import com.petproject.ecommerce.auth.service.RevokedTokenService;
import com.petproject.ecommerce.config.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final RevokedTokenService revokedTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthService authService, RevokedTokenService revokedTokenService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.revokedTokenService = revokedTokenService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        revokedTokenService.revoke(jwtTokenProvider.resolveToken(authHeader));
        return ResponseEntity.noContent().build();
    }
}

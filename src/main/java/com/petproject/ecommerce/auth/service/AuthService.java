package com.petproject.ecommerce.auth.service;

import com.petproject.ecommerce.auth.dto.request.LoginRequest;
import com.petproject.ecommerce.auth.dto.response.LoginResponse;
import com.petproject.ecommerce.config.JwtTokenProvider;
import com.petproject.ecommerce.exception.AuthenticationException;
import com.petproject.ecommerce.user.entity.User;
import com.petproject.ecommerce.user.enums.UserStatuses;
import com.petproject.ecommerce.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (user.getStatus() == UserStatuses.DISABLED || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user);
        return new LoginResponse(token);
    }
}

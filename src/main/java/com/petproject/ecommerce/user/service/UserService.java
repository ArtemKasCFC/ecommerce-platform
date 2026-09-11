package com.petproject.ecommerce.user.service;

import com.petproject.ecommerce.exception.AlreadyExistsException;
import com.petproject.ecommerce.user.dto.request.UserCreateRequest;
import com.petproject.ecommerce.user.dto.response.UserResponse;
import com.petproject.ecommerce.user.entity.User;
import com.petproject.ecommerce.user.enums.Statuses;
import com.petproject.ecommerce.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(UserCreateRequest request) {

        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new AlreadyExistsException("User with this email already exists");
        }

        String hash = passwordEncoder.encode(request.getPassword());

        User user = new User(normalizedEmail, request.getName(), hash, Statuses.ACTIVE, LocalDateTime.now());
        User savedUser = userRepository.save(user);

        return new UserResponse(savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getStatus(), savedUser.getCreatedAt());
    }
}

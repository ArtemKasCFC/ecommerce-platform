package com.petproject.ecommerce.user.service;

import com.petproject.ecommerce.exception.AlreadyExistsException;
import com.petproject.ecommerce.user.dto.request.UserCreateRequest;
import com.petproject.ecommerce.user.entity.User;
import com.petproject.ecommerce.user.enums.UserStatuses;
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

    public User createUser(UserCreateRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new AlreadyExistsException("User with this email already exists");
        }

        String hash = passwordEncoder.encode(request.getPassword());

        User user = new User(
                normalizedEmail,
                request.getName(),
                hash,
                UserStatuses.ACTIVE,
                LocalDateTime.now()
        );

        return userRepository.save(user);
    }
}

package com.petproject.ecommerce.user.controller;

import com.petproject.ecommerce.config.JwtTokenProvider;
import com.petproject.ecommerce.user.dto.request.UserCreateRequest;
import com.petproject.ecommerce.user.dto.response.RegistrationResponse;
import com.petproject.ecommerce.user.dto.response.UserResponse;
import com.petproject.ecommerce.user.entity.User;
import com.petproject.ecommerce.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping
    public ResponseEntity<RegistrationResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        User user = userService.createUser(request);

        String token = jwtTokenProvider.generateToken(user);

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getStatus(),
                user.getCreatedAt()
        );

        RegistrationResponse response = new RegistrationResponse(userResponse, token);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}

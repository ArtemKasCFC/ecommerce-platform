package com.petproject.ecommerce.tests;

import com.petproject.ecommerce.api.UserApi;
import com.petproject.ecommerce.assertions.UserAssertions;
import com.petproject.ecommerce.constants.ValidationMessages;
import com.petproject.ecommerce.database.UsersDb;
import com.petproject.ecommerce.factories.UserFactory;
import com.petproject.ecommerce.product.dto.response.ErrorResponse;
import com.petproject.ecommerce.user.dto.request.UserCreateRequest;
import com.petproject.ecommerce.user.dto.response.UserResponse;
import com.petproject.ecommerce.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class UsersTests {

    @Test
    void shouldCreateUserWithValidData() {
        UserCreateRequest body = UserFactory.defaultUser();
        UserResponse createdUser = UserApi.createUser(body, UserResponse.class, 201);

        UserAssertions.validateUserCreateResponse(createdUser, body);

        User userRecord = UsersDb.findById(createdUser.getId());
        assertThat(createdUser)
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(userRecord);

        assertThat(createdUser.getCreatedAt())
                .isCloseTo(userRecord.getCreatedAt(), within(1, ChronoUnit.MICROS));

        assertThat(userRecord.getPassword())
                .isNotEqualTo(body.getPassword())
                .startsWith("$2");
    }

    @Test
    void shouldNotCreateUserWithEmptyName() {
        UserCreateRequest body = UserFactory.defaultUser();
        body.setName("");
        ErrorResponse errorResponse = UserApi.createUser(body, ErrorResponse.class, 400);

        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("name", ValidationMessages.NAME_REQUIRED);
    }

    @Test
    void shouldNotCreateUserWithNameExceeding100Characters() {
        UserCreateRequest body = UserFactory.defaultUser();
        body.setName("A".repeat(101));
        ErrorResponse errorResponse = UserApi.createUser(body, ErrorResponse.class, 400);

        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("name", ValidationMessages.NAME_TOO_LONG);
    }

    @Test
    void shouldNotCreateUserWithEmptyEmail() {
        UserCreateRequest body = UserFactory.defaultUser();
        body.setEmail("");
        ErrorResponse errorResponse = UserApi.createUser(body, ErrorResponse.class, 400);

        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("email", ValidationMessages.EMAIL_REQUIRED);
    }

    @Test
    void shouldNotCreateUserWithDuplicateEmail() {
        UserCreateRequest body = UserFactory.defaultUser();
        UserApi.createUser(body, UserResponse.class, 201);

        ErrorResponse errorResponse = UserApi.createUser(body, ErrorResponse.class, 409);

        assertThat(errorResponse.getStatus()).isEqualTo(409);
        assertThat(errorResponse.getMessage()).isEqualTo(ValidationMessages.EMAIL_ALREADY_EXISTS);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-email",
            "test@",
            "@gmail.com",
            "test@gmail",
            "test@gmail."
    })
    void shouldNotCreateUserWithInvalidEmailFormat(String email) {
        UserCreateRequest body = UserFactory.defaultUser();
        body.setEmail(email);
        ErrorResponse errorResponse = UserApi.createUser(body, ErrorResponse.class, 400);

        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("email", ValidationMessages.EMAIL_INVALID);
    }

    @Test
    void shouldNotCreateUserWithEmptyPassword() {
        UserCreateRequest body = UserFactory.defaultUser();
        body.setPassword(null);
        ErrorResponse errorResponse = UserApi.createUser(body, ErrorResponse.class, 400);

        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("password", ValidationMessages.PASSWORD_REQUIRED);
    }

    @ParameterizedTest
    @MethodSource("invalidPasswords")
    void shouldNotCreateUserWithInvalidPassword(String password, String errorMessage) {
        UserCreateRequest body = UserFactory.defaultUser();
        body.setPassword(password);
        ErrorResponse errorResponse = UserApi.createUser(body, ErrorResponse.class, 400);

        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("password", errorMessage);
    }

    static Stream<Arguments> invalidPasswords() {
        return Stream.of(
                Arguments.of("Pass!23", ValidationMessages.PASSWORD_LENGTH_ERROR),
                Arguments.of("Pa!23".repeat(13), ValidationMessages.PASSWORD_LENGTH_ERROR),
                Arguments.of("pass!123", ValidationMessages.PASSWORD_INVALID),
                Arguments.of("PASS!123", ValidationMessages.PASSWORD_INVALID),
                Arguments.of("Pass!!!!", ValidationMessages.PASSWORD_INVALID),
                Arguments.of("Pass1234", ValidationMessages.PASSWORD_INVALID)
        );
    }
}

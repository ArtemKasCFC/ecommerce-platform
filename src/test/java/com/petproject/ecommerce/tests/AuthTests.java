package com.petproject.ecommerce.tests;

import com.petproject.ecommerce.api.AuthApi;
import com.petproject.ecommerce.api.ProductApi;
import com.petproject.ecommerce.auth.dto.request.LoginRequest;
import com.petproject.ecommerce.auth.dto.response.LoginResponse;
import com.petproject.ecommerce.constants.ValidationMessages;
import com.petproject.ecommerce.factories.ProductFactory;
import com.petproject.ecommerce.product.dto.request.ProductCreateRequest;
import com.petproject.ecommerce.product.dto.response.ErrorResponse;
import com.petproject.ecommerce.steps.UserSteps;
import com.petproject.ecommerce.utils.JwtTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AuthTests {


    @Test
    void shouldLoginWithValidCredentialsAsUser() {
        Map<String, String> credentials = UserSteps.createUser();
        LoginRequest request = LoginRequest.builder()
                .email(credentials.get("email"))
                .password(credentials.get("password"))
                .build();

        LoginResponse response = AuthApi.login(request, LoginResponse.class, 200);
        assertThat(response.getToken()).isNotBlank();
    }

    @Test
    void shouldLoginWithValidCredentialsAsAdmin() {
        Map<String, String> credentials = UserSteps.createAdmin();
        LoginRequest request = LoginRequest.builder()
                .email(credentials.get("email"))
                .password(credentials.get("password"))
                .build();

        LoginResponse response = AuthApi.login(request, LoginResponse.class, 200);
        assertThat(response.getToken()).isNotBlank();
    }

    @Test
    void shouldNotLoginWithInvalidPassword() {
        Map<String, String> credentials = UserSteps.createUser();
        LoginRequest request = LoginRequest.builder()
                .email(credentials.get("email"))
                .password("NotRea!Pa22")
                .build();

        ErrorResponse errorResponse = AuthApi.login(request, ErrorResponse.class, 401);
        assertThat(errorResponse.getStatus()).isEqualTo(401);
        assertThat(errorResponse.getMessage()).isEqualTo(ValidationMessages.INVALID_CREDENTIALS);
    }

    @Test
    void shouldNotLoginWithNonExistingEmail() {
        LoginRequest request = LoginRequest.builder()
                .email("not.real.email@gmail.com")
                .password("NotRea!Pa22")
                .build();

        ErrorResponse errorResponse = AuthApi.login(request, ErrorResponse.class, 401);
        assertThat(errorResponse.getStatus()).isEqualTo(401);
        assertThat(errorResponse.getMessage()).isEqualTo(ValidationMessages.INVALID_CREDENTIALS);
    }

    @Test
    void shouldNotLoginWithDisabledUser() {
        Map<String, String> credentials = UserSteps.createDisabledUser();
        LoginRequest request = LoginRequest.builder()
                .email(credentials.get("email"))
                .password(credentials.get("password"))
                .build();

        ErrorResponse errorResponse = AuthApi.login(request, ErrorResponse.class, 401);
        assertThat(errorResponse.getStatus()).isEqualTo(401);
        assertThat(errorResponse.getMessage()).isEqualTo(ValidationMessages.INVALID_CREDENTIALS);
    }

    @Test
    void shouldNotLoginWithEmptyPassword() {
        Map<String, String> credentials = UserSteps.createUser();
        LoginRequest request = LoginRequest.builder()
                .email(credentials.get("email"))
                .password(null)
                .build();

        ErrorResponse errorResponse = AuthApi.login(request, ErrorResponse.class, 400);
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors().containsValue(ValidationMessages.PASSWORD_REQUIRED));
    }

    @Test
    void shouldNotLoginWithEmptyEmail() {
        LoginRequest request = LoginRequest.builder()
                .email(null)
                .password("JustAPass1!")
                .build();

        ErrorResponse errorResponse = AuthApi.login(request, ErrorResponse.class, 400);
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors().containsValue(ValidationMessages.EMAIL_REQUIRED));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-email",
            "test@",
            "@gmail.com",
            "test@gmail",
            "test@gmail."
    })
    void shouldNotLoginWithInvalidEmail(String email) {
        LoginRequest request = LoginRequest.builder()
                .email(email)
                .password("JustAPass1!")
                .build();

        ErrorResponse errorResponse = AuthApi.login(request, ErrorResponse.class, 400);
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors().containsValue(ValidationMessages.EMAIL_INVALID));
    }

    @Test
    void shouldLogoutSuccessfullyAsUser() {
        String userToken = UserSteps.createUser().get("token");
        AuthApi.logout(userToken, Void.class, 204);
    }

    @Test
    void shouldLogoutSuccessfullyAsAdmin() {
        String adminToken = UserSteps.createAdmin().get("token");
        AuthApi.logout(adminToken, Void.class, 204);
    }

    @Test
    void shouldNotLogoutWithoutAuthentication() {
        ErrorResponse errorResponse = AuthApi.logout("", ErrorResponse.class, 401);

        assertThat(errorResponse.getStatus()).isEqualTo(401);
        assertThat(errorResponse.getMessage()).isEqualTo(ValidationMessages.AUTH_REQUIRED);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-token",
            "just.invalid.token."
    })
    void shouldNotAccessProtectedEndpointWithInvalidToken(String token) {
        ErrorResponse errorResponse = AuthApi.logout(token, ErrorResponse.class, 401);

        assertThat(errorResponse.getStatus()).isEqualTo(401);
        assertThat(errorResponse.getMessage()).isEqualTo(ValidationMessages.INVALID_TOKEN);
    }

    @Test
    void shouldNotAccessProtectedEndpointWithModifiedToken() {
        Map<String, String> credentials = UserSteps.createUser();

        String payload = JwtTestUtils.getPayload(credentials.get("token"));
        String modifiedPayload = payload.replace("USER", "ADMIN");

        String modifiedToken = JwtTestUtils.replacePayload(
                credentials.get("token"),
                modifiedPayload
        );

        ProductCreateRequest productCreateRequest = ProductFactory.defaultProduct();
        ErrorResponse errorResponse = ProductApi.createProduct(productCreateRequest, modifiedToken, ErrorResponse.class, 401);

        assertThat(errorResponse.getStatus()).isEqualTo(401);
        assertThat(errorResponse.getMessage()).isEqualTo(ValidationMessages.INVALID_TOKEN);
    }

    @Test
    void shouldNotAccessProtectedEndpointWithRevokedToken() {
        String token = UserSteps.createAdmin().get("token");

        AuthApi.logout(token, Void.class, 204);

        ProductCreateRequest productCreateRequest = ProductFactory.defaultProduct();
        ErrorResponse errorResponse = ProductApi.createProduct(productCreateRequest, token, ErrorResponse.class, 401);

        assertThat(errorResponse.getStatus()).isEqualTo(401);
        assertThat(errorResponse.getMessage()).isEqualTo(ValidationMessages.INVALID_TOKEN);
    }

    @Test
    void shouldNotAccessProtectedEndpointWithExpiredToken() {
        String token = JwtTestUtils.createExpiredToken();

        ProductCreateRequest productCreateRequest = ProductFactory.defaultProduct();
        ErrorResponse errorResponse = ProductApi.createProduct(productCreateRequest, token, ErrorResponse.class, 401);

        assertThat(errorResponse.getStatus()).isEqualTo(401);
        assertThat(errorResponse.getMessage()).isEqualTo(ValidationMessages.INVALID_TOKEN);
    }
}

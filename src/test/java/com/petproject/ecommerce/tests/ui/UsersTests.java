package com.petproject.ecommerce.tests.ui;

import com.petproject.ecommerce.constants.Pages;
import com.petproject.ecommerce.constants.UserEnpoints;
import com.petproject.ecommerce.constants.ValidationMessages;
import com.petproject.ecommerce.factories.UserFactory;
import com.petproject.ecommerce.pages.Navbar;
import com.petproject.ecommerce.pages.RegistrationPage;
import com.petproject.ecommerce.user.dto.request.UserCreateRequest;
import com.petproject.ecommerce.utils.NetworkUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.module.Network;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class UsersTests {

    private WebDriver driver;
    private WebDriverWait wait;
    private Network network;
    private RegistrationPage registrationPage;
    private Navbar navbar;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.setCapability("webSocketUrl", true);

        driver = new ChromeDriver(options);
        network = new Network(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        registrationPage = new RegistrationPage(driver, wait);
        navbar = new Navbar(wait);

        registrationPage.visit();
    }

    @AfterEach
    void quit() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void shouldRegisterUserWithValidData() {
        UserCreateRequest request = UserFactory.defaultUser();

        CompletableFuture<Integer> responseStatusFuture = NetworkUtils.listenForResponseStatus(network, "POST", UserEnpoints.USERS);

        registrationPage.register(request);

        int loginStatusCode = NetworkUtils.awaitResponseStatus(responseStatusFuture);
        assertThat(loginStatusCode).isEqualTo(201);

        String token = NetworkUtils.getToken(driver);
        assertThat(token).isNotBlank();

        wait.until(ExpectedConditions.urlToBe(Pages.MAIN_PAGE));
        navbar.validateAuthState(true);
    }

    @Test
    void shouldNotRegisterUserWithEmptyName() {
        UserCreateRequest request = UserFactory.defaultUser();

        registrationPage.fillInEmail(request.getEmail());
        registrationPage.fillInPassword(request.getPassword());
        registrationPage.clickLoginBtn();
        registrationPage.validateFieldError("name", ValidationMessages.NAME_REQUIRED_UI);

        wait.until(ExpectedConditions.urlToBe(Pages.REGISTRATION_PAGE));
        navbar.validateAuthState(false);
    }

    @Test
    void shouldNotRegisterUserWithNameExceeding100Characters() {
        UserCreateRequest request = UserFactory.defaultUser();
        request.setName("A".repeat(101));

        registrationPage.register(request);
        registrationPage.validateFieldError("name", ValidationMessages.NAME_TOO_LONG_UI);

        wait.until(ExpectedConditions.urlToBe(Pages.REGISTRATION_PAGE));
        navbar.validateAuthState(false);
    }

    @Test
    void shouldNotRegisterUserWithEmptyEmail() {
        UserCreateRequest request = UserFactory.defaultUser();

        registrationPage.fillInName(request.getName());
        registrationPage.fillInPassword(request.getPassword());
        registrationPage.clickLoginBtn();
        registrationPage.validateFieldError("email", ValidationMessages.EMAIL_REQUIRED_UI);

        wait.until(ExpectedConditions.urlToBe(Pages.REGISTRATION_PAGE));
        navbar.validateAuthState(false);
    }

    @Test
    void shouldNotRegisterUserWithDuplicateEmail() {
        UserCreateRequest request = UserFactory.defaultUser();

        registrationPage.register(request);
        wait.until(ExpectedConditions.urlToBe(Pages.MAIN_PAGE));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[normalize-space()='Logout']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a[href='/register']"))).click();
        wait.until(ExpectedConditions.urlToBe(Pages.REGISTRATION_PAGE));

        CompletableFuture<Integer> responseStatusFuture = NetworkUtils.listenForResponseStatus(network, "POST", UserEnpoints.USERS);

        registrationPage.register(request);

        int loginStatusCode = NetworkUtils.awaitResponseStatus(responseStatusFuture);
        assertThat(loginStatusCode).isEqualTo(409);

        String token = NetworkUtils.getToken(driver);
        assertThat(token).isNull();

        registrationPage.validateFieldError("email", ValidationMessages.EMAIL_ALREADY_EXISTS_UI);

        wait.until(ExpectedConditions.urlToBe(Pages.REGISTRATION_PAGE));
        navbar.validateAuthState(false);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-email",
            "test@",
            "@gmail.com",
            "test@gmail",
            "test@gmail."
    })
    void shouldNotRegisterUserWithInvalidEmailFormat(String email) {
        UserCreateRequest request = UserFactory.defaultUser();
        request.setEmail(email);
        registrationPage.register(request);
        registrationPage.validateFieldError("email", ValidationMessages.EMAIL_INVALID_UI);

        wait.until(ExpectedConditions.urlToBe(Pages.REGISTRATION_PAGE));
        navbar.validateAuthState(false);
    }

    @Test
    void shouldNotRegisterUserWithEmptyPassword() {
        UserCreateRequest request = UserFactory.defaultUser();

        registrationPage.fillInName(request.getName());
        registrationPage.fillInEmail(request.getEmail());
        registrationPage.clickLoginBtn();
        registrationPage.validateFieldError("password", ValidationMessages.PASSWORD_REQUIRED_UI);

        wait.until(ExpectedConditions.urlToBe(Pages.REGISTRATION_PAGE));
        navbar.validateAuthState(false);
    }

    @ParameterizedTest
    @MethodSource("invalidPasswords")
    void shouldNotRegisterUserWithInvalidPassword(String password, String errorMessage) {
        UserCreateRequest request = UserFactory.defaultUser();
        request.setPassword(password);
        registrationPage.register(request);

        registrationPage.validateFieldError("password", errorMessage);

        wait.until(ExpectedConditions.urlToBe(Pages.REGISTRATION_PAGE));
        navbar.validateAuthState(false);
    }

    static Stream<Arguments> invalidPasswords() {
        return Stream.of(
                Arguments.of("Pass!23", ValidationMessages.PASSWORD_LENGTH_ERROR_UI),
                Arguments.of("Pa!23".repeat(13), ValidationMessages.PASSWORD_LENGTH_ERROR_UI),
                Arguments.of("pass!123", ValidationMessages.PASSWORD_UPPERCASE_REQUIRED),
                Arguments.of("PASS!123", ValidationMessages.PASSWORD_LOWERCASE_REQUIRED),
                Arguments.of("Pass!!!!", ValidationMessages.PASSWORD_DIGIT_REQUIRED),
                Arguments.of("Pass1234", ValidationMessages.PASSWORD_SPEC_CHAR_REQUIRED)
        );
    }

    @Test
    void shouldClearValidationErrorsWhenCorrectingFields() {
        registrationPage.clickLoginBtn();

        registrationPage.validateFieldError("name", ValidationMessages.NAME_REQUIRED_UI);
        registrationPage.validateFieldError("email", ValidationMessages.EMAIL_REQUIRED_UI);
        registrationPage.validateFieldError("password", ValidationMessages.PASSWORD_REQUIRED_UI);

        UserCreateRequest request = UserFactory.defaultUser();

        registrationPage.fillInName(request.getName());
        registrationPage.validateFieldErrorIsNotDisplayed("name");

        registrationPage.fillInEmail(request.getEmail());
        registrationPage.validateFieldErrorIsNotDisplayed("email");

        registrationPage.fillInPassword(request.getPassword());
        registrationPage.validateFieldErrorIsNotDisplayed("password");

        CompletableFuture<Integer> responseStatusFuture = NetworkUtils.listenForResponseStatus(network, "POST", UserEnpoints.USERS);

        registrationPage.clickLoginBtn();

        int loginStatusCode = NetworkUtils.awaitResponseStatus(responseStatusFuture);
        assertThat(loginStatusCode).isEqualTo(201);

        String token = NetworkUtils.getToken(driver);
        assertThat(token).isNotBlank();

        wait.until(ExpectedConditions.urlToBe(Pages.MAIN_PAGE));
        navbar.validateAuthState(true);
    }
}

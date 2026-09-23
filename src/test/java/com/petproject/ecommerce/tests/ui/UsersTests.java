package com.petproject.ecommerce.tests.ui;

import com.petproject.ecommerce.constants.Pages;
import com.petproject.ecommerce.constants.ValidationMessages;
import com.petproject.ecommerce.factories.UserFactory;
import com.petproject.ecommerce.pages.RegistrationPage;
import com.petproject.ecommerce.user.dto.request.UserCreateRequest;
import com.petproject.ecommerce.utils.WaitUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class UsersTests {

    private WebDriver driver;
    private RegistrationPage registrationPage;
    private WaitUtils waitUtils;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        waitUtils = new WaitUtils(driver);
        registrationPage = new RegistrationPage(driver, waitUtils);
    }

    @AfterEach
    void quit() {
        driver.quit();
    }

    @Test
    void shouldRegisterUserWithValidData() {
        UserCreateRequest request = UserFactory.defaultUser();
        registrationPage.open().register(request);

        waitUtils.waitUrl(Pages.MAIN_PAGE);

        assertThat(driver.getCurrentUrl()).isEqualTo(Pages.MAIN_PAGE);
    }

    @Test
    void shouldNotRegisterUserWithEmptyName() {
        UserCreateRequest request = UserFactory.defaultUser();
        request.setName("");
        registrationPage.open().register(request);

        assertThat(registrationPage.getErrorText(RegistrationPage.NAME_ERROR)).isEqualTo(ValidationMessages.NAME_REQUIRED_UI);

        assertThat(registrationPage.getInvalidField("#name").isDisplayed()).isTrue();
    }

    @Test
    void shouldNotRegisterUserWithNameExceeding100Characters() {
        UserCreateRequest request = UserFactory.defaultUser();
        request.setName("A".repeat(101));
        registrationPage.open().register(request);

        assertThat(registrationPage.getErrorText(RegistrationPage.NAME_ERROR)).isEqualTo(ValidationMessages.NAME_TOO_LONG_UI);

        assertThat(registrationPage.getInvalidField("#name").isDisplayed()).isTrue();
    }

    @Test
    void shouldNotRegisterUserWithEmptyEmail() {
        UserCreateRequest request = UserFactory.defaultUser();
        request.setEmail("");
        registrationPage.open().register(request);

        assertThat(registrationPage.getErrorText(RegistrationPage.EMAIL_ERROR)).isEqualTo(ValidationMessages.EMAIL_REQUIRED_UI);

        assertThat(registrationPage.getInvalidField("#email").isDisplayed()).isTrue();
    }

    @Test
    void shouldNotRegisterUserWithDuplicateEmail() {
        UserCreateRequest request = UserFactory.defaultUser();
        registrationPage.open().register(request);

        waitUtils.waitUrl(Pages.MAIN_PAGE);

        driver.findElement(By.linkText("Register")).click();
        waitUtils.waitUrl(Pages.REGISTRATION_PAGE);

        registrationPage.register(request);

        assertThat(registrationPage.getErrorText(RegistrationPage.EMAIL_ERROR)).isEqualTo(ValidationMessages.EMAIL_ALREADY_EXISTS_UI);

        assertThat(registrationPage.getInvalidField("#email").isDisplayed()).isTrue();
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
        registrationPage.open().register(request);

        assertThat(registrationPage.getErrorText(RegistrationPage.EMAIL_ERROR)).isEqualTo(ValidationMessages.EMAIL_INVALID_UI);

        assertThat(registrationPage.getInvalidField("#email").isDisplayed()).isTrue();
    }

    @Test
    void shouldNotRegisterUserWithEmptyPassword() {
        UserCreateRequest request = UserFactory.defaultUser();
        request.setPassword("");
        registrationPage.open().register(request);

        assertThat(registrationPage.getErrorText(RegistrationPage.PASSWORD_ERROR)).isEqualTo(ValidationMessages.PASSWORD_REQUIRED_UI);

        assertThat(registrationPage.getInvalidField("#password").isDisplayed()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("invalidPasswords")
    void shouldNotRegisterUserWithInvalidPassword(String password, String errorMessage) {
        UserCreateRequest request = UserFactory.defaultUser();
        request.setPassword(password);
        registrationPage.open().register(request);

        assertThat(registrationPage.getErrorText(RegistrationPage.PASSWORD_ERROR)).isEqualTo(errorMessage);

        assertThat(registrationPage.getInvalidField("#password").isDisplayed()).isTrue();
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
        registrationPage.open();
        driver.findElement(RegistrationPage.CREATE_ACCOUNT_BTN).click();

        assertThat(registrationPage.getErrorText(RegistrationPage.NAME_ERROR)).isEqualTo(ValidationMessages.NAME_REQUIRED_UI);
        assertThat(registrationPage.getInvalidField("#name").isDisplayed()).isTrue();
        assertThat(registrationPage.getErrorText(RegistrationPage.EMAIL_ERROR)).isEqualTo(ValidationMessages.EMAIL_REQUIRED_UI);
        assertThat(registrationPage.getInvalidField("#email").isDisplayed()).isTrue();
        assertThat(registrationPage.getErrorText(RegistrationPage.PASSWORD_ERROR)).isEqualTo(ValidationMessages.PASSWORD_REQUIRED_UI);
        assertThat(registrationPage.getInvalidField("#password").isDisplayed()).isTrue();

        UserCreateRequest request = UserFactory.defaultUser();

        driver.findElement(RegistrationPage.NAME_FIELD).sendKeys(request.getName());
        assertThat(driver.findElements(RegistrationPage.NAME_ERROR)).isEmpty();
        assertThat(driver.findElements(By.cssSelector("#name" + RegistrationPage.CLASS_OF_IS_INVALID_ICON))).isEmpty();

        driver.findElement(RegistrationPage.EMAIL_FIELD).sendKeys(request.getEmail());
        assertThat(driver.findElements(RegistrationPage.EMAIL_ERROR)).isEmpty();
        assertThat(driver.findElements(By.cssSelector("#email" + RegistrationPage.CLASS_OF_IS_INVALID_ICON))).isEmpty();

        driver.findElement(RegistrationPage.PASSWORD_FIELD).sendKeys(request.getPassword());
        assertThat(driver.findElements(RegistrationPage.PASSWORD_ERROR)).isEmpty();
        assertThat(driver.findElements(By.cssSelector("#password" + RegistrationPage.CLASS_OF_IS_INVALID_ICON))).isEmpty();

        driver.findElement(RegistrationPage.CREATE_ACCOUNT_BTN).click();

        waitUtils.waitUrl(Pages.MAIN_PAGE);
        assertThat(driver.getCurrentUrl()).isEqualTo(Pages.MAIN_PAGE);
    }
}

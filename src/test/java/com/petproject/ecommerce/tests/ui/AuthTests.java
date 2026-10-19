package com.petproject.ecommerce.tests.ui;

import com.petproject.ecommerce.api.ProductApi;
import com.petproject.ecommerce.config.PropertiesReader;
import com.petproject.ecommerce.constants.AuthEndpoints;
import com.petproject.ecommerce.constants.Pages;
import com.petproject.ecommerce.constants.ValidationMessages;
import com.petproject.ecommerce.factories.ProductFactory;
import com.petproject.ecommerce.pages.LoginPage;
import com.petproject.ecommerce.pages.Navbar;
import com.petproject.ecommerce.product.dto.response.ErrorResponse;
import com.petproject.ecommerce.utils.NetworkUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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

import static org.assertj.core.api.Assertions.assertThat;

public class AuthTests {

    private WebDriver driver;
    private Network network;
    private LoginPage loginPage;
    private Navbar navbar;
    private WebDriverWait wait;
    private static String userEmail;
    private static String userPassword;
    private static String adminEmail;
    private static String adminPassword;
    private static String disabledUserEmail;
    private static String disabledUserPassword;

    @BeforeAll
    static void getCredentials() {
        userEmail = PropertiesReader.get("user.email");
        userPassword = PropertiesReader.get("user.password");
        adminEmail = PropertiesReader.get("admin.email");
        adminPassword = PropertiesReader.get("admin.password");
        disabledUserEmail = PropertiesReader.get("disabled-user.email");
        disabledUserPassword = PropertiesReader.get("disabled-user.password");
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.setCapability("webSocketUrl", true);

        driver = new ChromeDriver(options);
        network = new Network(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        loginPage = new LoginPage(driver, wait);
        navbar = new Navbar(wait);

        loginPage.visit();
    }

    @AfterEach
    void quit() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void shouldLoginWithValidCredentialsAsUser() {
        CompletableFuture<Integer> responseStatusFuture = NetworkUtils.listenForResponseStatus(network, "POST", AuthEndpoints.LOGIN);

        loginPage.login(AuthTests.userEmail, AuthTests.userPassword);

        int loginStatusCode = NetworkUtils.awaitResponseStatus(responseStatusFuture);
        assertThat(loginStatusCode).isEqualTo(200);

        String token = NetworkUtils.getToken(driver);
        assertThat(token).isNotBlank();

        wait.until(ExpectedConditions.urlToBe(Pages.MAIN_PAGE));
        navbar.validateAuthState(true);
    }

    @Test
    void shouldLoginWithValidCredentialsAsAdmin() {
        CompletableFuture<Integer> responseStatusFuture = NetworkUtils.listenForResponseStatus(network, "POST", AuthEndpoints.LOGIN);

        loginPage.login(AuthTests.adminEmail, AuthTests.adminPassword);

        int loginStatusCode = NetworkUtils.awaitResponseStatus(responseStatusFuture);
        assertThat(loginStatusCode).isEqualTo(200);

        String token = NetworkUtils.getToken(driver);
        assertThat(token).isNotBlank();

        wait.until(ExpectedConditions.urlToBe(Pages.MAIN_PAGE));
        navbar.validateAuthState(true);
    }

    @Test
    void shouldRemainAuthenticatedAfterPageReload() {
        CompletableFuture<Integer> responseStatusFuture = NetworkUtils.listenForResponseStatus(network, "POST", AuthEndpoints.LOGIN);

        loginPage.login(AuthTests.userEmail, AuthTests.userPassword);

        int loginStatusCode = NetworkUtils.awaitResponseStatus(responseStatusFuture);
        assertThat(loginStatusCode).isEqualTo(200);

        String token = NetworkUtils.getToken(driver);
        assertThat(token).isNotBlank();

        wait.until(ExpectedConditions.urlToBe(Pages.MAIN_PAGE));
        navbar.validateAuthState(true);

        driver.navigate().refresh();

        navbar.validateAuthState(true);

        String tokenAfterReload = NetworkUtils.getToken(driver);
        assertThat(tokenAfterReload).isEqualTo(token);
    }

    @Test
    void shouldNotLoginWithIncorrectPassword() {
        CompletableFuture<Integer> responseStatusFuture = NetworkUtils.listenForResponseStatus(network, "POST", AuthEndpoints.LOGIN);

        loginPage.login(AuthTests.userEmail, "IncorrectPassword123!");
        loginPage.validateFormError(ValidationMessages.INVALID_CREDENTIALS);

        int loginStatusCode = NetworkUtils.awaitResponseStatus(responseStatusFuture);
        assertThat(loginStatusCode).isEqualTo(401);

        String token = NetworkUtils.getToken(driver);
        assertThat(token).isNull();

        wait.until(ExpectedConditions.urlToBe(Pages.LOGIN_PAGE));
        navbar.validateAuthState(false);
    }


    @Test
    void shouldNotLoginWithNonExistingEmail() {
        CompletableFuture<Integer> responseStatusFuture = NetworkUtils.listenForResponseStatus(network, "POST", AuthEndpoints.LOGIN);

        loginPage.login("doesntexist@gmail.comn", AuthTests.userPassword);
        loginPage.validateFormError(ValidationMessages.INVALID_CREDENTIALS);

        int loginStatusCode = NetworkUtils.awaitResponseStatus(responseStatusFuture);
        assertThat(loginStatusCode).isEqualTo(401);

        String token = NetworkUtils.getToken(driver);
        assertThat(token).isNull();

        wait.until(ExpectedConditions.urlToBe(Pages.LOGIN_PAGE));
        navbar.validateAuthState(false);
    }

    @Test
    void shouldNotLoginWithDisabledUser() {
        CompletableFuture<Integer> responseStatusFuture = NetworkUtils.listenForResponseStatus(network, "POST", AuthEndpoints.LOGIN);

        loginPage.login(AuthTests.disabledUserEmail, AuthTests.disabledUserPassword);
        loginPage.validateFormError(ValidationMessages.INVALID_CREDENTIALS);

        int loginStatusCode = NetworkUtils.awaitResponseStatus(responseStatusFuture);
        assertThat(loginStatusCode).isEqualTo(401);

        String token = NetworkUtils.getToken(driver);
        assertThat(token).isNull();

        wait.until(ExpectedConditions.urlToBe(Pages.LOGIN_PAGE));
        navbar.validateAuthState(false);
    }

    @Test
    void shouldNotLoginWithEmptyEmail() {
        loginPage.fillInPassword(AuthTests.userPassword);
        loginPage.clickLoginBtn();
        loginPage.validateFieldError("email", ValidationMessages.EMAIL_REQUIRED_UI);

        wait.until(ExpectedConditions.urlToBe(Pages.LOGIN_PAGE));
        navbar.validateAuthState(false);
    }

    @Test
    void shouldNotLoginWithEmptyPassword() {
        loginPage.fillInEmail(AuthTests.userEmail);
        loginPage.clickLoginBtn();
        loginPage.validateFieldError("password", ValidationMessages.PASSWORD_REQUIRED_UI);

        wait.until(ExpectedConditions.urlToBe(Pages.LOGIN_PAGE));
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
    void shouldNotLoginWithInvalidEmailFormat(String email) {
        loginPage.login(email, AuthTests.userPassword);
        loginPage.validateFieldError("email", ValidationMessages.EMAIL_INVALID);

        wait.until(ExpectedConditions.urlToBe(Pages.LOGIN_PAGE));
        navbar.validateAuthState(false);
    }

    @Test
    void shouldLogoutSuccessfullyAsUser() {
        CompletableFuture<Integer> responseStatusFuture = NetworkUtils.listenForResponseStatus(network, "POST", AuthEndpoints.LOGOUT);

        loginPage.login(AuthTests.userEmail, AuthTests.userPassword);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[normalize-space()='Logout']"))).click();

        int logoutStatusCode = NetworkUtils.awaitResponseStatus(responseStatusFuture);
        assertThat(logoutStatusCode).isEqualTo(204);

        String token = NetworkUtils.getToken(driver);
        assertThat(token).isNull();

        wait.until(ExpectedConditions.urlToBe(Pages.MAIN_PAGE));
        navbar.validateAuthState(false);
    }

    @Test
    void shouldRevokeTokenAfterLogout() {
        CompletableFuture<Integer> responseStatusFuture = NetworkUtils.listenForResponseStatus(network, "POST", AuthEndpoints.LOGOUT);

        loginPage.login(AuthTests.adminEmail, AuthTests.adminPassword);

        wait.until(ExpectedConditions.urlToBe(Pages.MAIN_PAGE));
        String token = NetworkUtils.getToken(driver);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[normalize-space()='Logout']"))).click();

        int logoutStatusCode = NetworkUtils.awaitResponseStatus(responseStatusFuture);
        assertThat(logoutStatusCode).isEqualTo(204);

        ErrorResponse errorResponse = ProductApi.createProduct(ProductFactory.defaultProduct(), token, ErrorResponse.class, 401);
        assertThat(errorResponse.getStatus()).isEqualTo(401);
        assertThat(errorResponse.getMessage()).isEqualTo(ValidationMessages.INVALID_TOKEN);
    }
}

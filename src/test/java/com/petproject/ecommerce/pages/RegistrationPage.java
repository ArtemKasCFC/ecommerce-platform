package com.petproject.ecommerce.pages;

import com.petproject.ecommerce.constants.Pages;
import com.petproject.ecommerce.user.dto.request.UserCreateRequest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrationPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public RegistrationPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void visit() {
        driver.get(Pages.REGISTRATION_PAGE);
    }

    public void fillInName(String name) {
        driver.findElement(By.cssSelector("#name")).sendKeys(name);
    }

    public void fillInEmail(String email) {
        driver.findElement(By.cssSelector("#email")).sendKeys(email);
    }

    public void fillInPassword(String password) {
        driver.findElement(By.cssSelector("#password")).sendKeys(password);
    }

    public void clickLoginBtn() {
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }

    public void register(UserCreateRequest request) {
        fillInName(request.getName());
        fillInEmail(request.getEmail());
        fillInPassword(request.getPassword());

        clickLoginBtn();
    }

    public void validateFieldError(String field, String message) {
        String textError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#%s ~ .invalid-feedback".formatted(field)))).getText();
        assertThat(textError).isEqualTo(message);

        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#%s".formatted(field))));
        assertThat(input.getAttribute("class")).contains("is-invalid");
    }

    public void validateFieldErrorIsNotDisplayed(String field) {
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector("#%s ~ .invalid-feedback".formatted(field)), 0));

        WebElement input = driver.findElement(By.cssSelector("#%s".formatted(field)));
        wait.until(ExpectedConditions.not(ExpectedConditions.attributeContains(input, "class", "is-invalid")));
    }
}

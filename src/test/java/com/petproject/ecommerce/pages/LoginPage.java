package com.petproject.ecommerce.pages;

import com.petproject.ecommerce.constants.Pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void visit() {
        driver.get(Pages.LOGIN_PAGE);
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

    public void login(String email, String password) {
        fillInEmail(email);
        fillInPassword(password);

        clickLoginBtn();
    }

    public void validateFieldError(String field, String message) {
        String textError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#%s ~ .invalid-feedback".formatted(field)))).getText();
        assertThat(textError).isEqualTo(message);

        WebElement errorIcon = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#%s".formatted(field))));
        assertThat(errorIcon.getAttribute("class")).contains("is-invalid");
    }

    public void validateFormError(String message) {
        String textError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-danger"))).getText();
        assertThat(textError).isEqualTo(message);
    }
}


package com.petproject.ecommerce.pages;

import com.petproject.ecommerce.constants.Pages;
import com.petproject.ecommerce.user.dto.request.UserCreateRequest;
import com.petproject.ecommerce.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class RegistrationPage {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    public static final By NAME_FIELD = By.id("name");
    public static final By EMAIL_FIELD = By.id("email");
    public static final By PASSWORD_FIELD = By.id("password");
    public static final By CREATE_ACCOUNT_BTN = By.cssSelector("button[type=submit]");
    public static final By NAME_ERROR = By.cssSelector("#name ~ .invalid-feedback");
    public static final By EMAIL_ERROR = By.cssSelector("#email ~ .invalid-feedback");
    public static final By PASSWORD_ERROR = By.cssSelector("#password ~ .invalid-feedback");
    public static final String CLASS_OF_IS_INVALID_ICON = ".is-invalid";

    public RegistrationPage(WebDriver driver, WaitUtils waitUtils) {
        this.driver = driver;
        this.waitUtils = waitUtils;
    }


    public RegistrationPage open() {
        driver.get(Pages.REGISTRATION_PAGE);
        return this;
    }

    public void register(UserCreateRequest request) {
        driver.findElement(NAME_FIELD).sendKeys(request.getName());
        driver.findElement(EMAIL_FIELD).sendKeys(request.getEmail());
        driver.findElement(PASSWORD_FIELD).sendKeys(request.getPassword());

        driver.findElement(CREATE_ACCOUNT_BTN).click();
    }

    public WebElement getInvalidField(String id) {
        return driver.findElement(By.cssSelector(id + CLASS_OF_IS_INVALID_ICON));
    }

    public String getErrorText(By locator) {
        waitUtils.waitElement(locator);
        return driver.findElement(locator).getText();
    }
}

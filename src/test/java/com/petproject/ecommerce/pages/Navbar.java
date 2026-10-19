package com.petproject.ecommerce.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Navbar {
    private final WebDriverWait wait;

    public Navbar(WebDriverWait wait) {
        this.wait = wait;
    }

    public void validateAuthState(boolean isLoggedIn) {
        if (isLoggedIn) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[normalize-space()='Logout']")));
            wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector("a[href='/register']"), 0));
            wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector("a[href='/login']"), 0));
        } else {
            wait.until(ExpectedConditions.numberOfElementsToBe(By.xpath("//button[normalize-space()='Logout']"), 0));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a[href='/register']")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a[href='/login']")));
        }
    }
}

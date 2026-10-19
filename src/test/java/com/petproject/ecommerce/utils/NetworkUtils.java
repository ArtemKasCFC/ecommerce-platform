package com.petproject.ecommerce.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.module.Network;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NetworkUtils {

    public static CompletableFuture<Integer> listenForResponseStatus(Network network, String method, String urlSuffix) {

        CompletableFuture<Integer> responseStatusFuture = new CompletableFuture<>();

        network.onResponseCompleted(event -> {
            if (method.equals(event.getRequest().getMethod()) && event.getResponseData().getUrl().endsWith(urlSuffix)) {
                responseStatusFuture.complete(event.getResponseData().getStatus());
            }
        });

        return responseStatusFuture;
    }

    public static int awaitResponseStatus(CompletableFuture<Integer> responseStatusFuture) {
        try {
            return responseStatusFuture.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw new AssertionError("Timed out waiting for the network response", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Interrupted while waiting for the network response", e);
        } catch (ExecutionException e) {
            throw new AssertionError("Failed to receive the network response", e.getCause());
        }
    }

    public static String getToken(WebDriver driver) {
        return (String) ((JavascriptExecutor) driver).executeScript("return window.localStorage.getItem('token');");
    }
}
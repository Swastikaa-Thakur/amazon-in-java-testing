package com.amazon.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Centralised explicit-wait utilities for Amazon.in tests.
 */
public class WaitUtils {

    private static final int DEFAULT_TIMEOUT = 15;

    // ── VISIBILITY ─────────────────────────────────────────────────
    public static WebElement waitForVisible(WebDriver driver, By locator) {
        return waitForVisible(driver, locator, DEFAULT_TIMEOUT);
    }

    public static WebElement waitForVisible(WebDriver driver, By locator, int seconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(seconds))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // ── CLICKABLE ──────────────────────────────────────────────────
    public static WebElement waitForClickable(WebDriver driver, By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForClickable(WebDriver driver, WebElement element) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(ExpectedConditions.elementToBeClickable(element));
    }

    // ── PRESENCE ───────────────────────────────────────────────────
    public static WebElement waitForPresence(WebDriver driver, By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // ── URL ────────────────────────────────────────────────────────
    public static boolean waitForUrlContains(WebDriver driver, String fragment) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(ExpectedConditions.urlContains(fragment));
    }

    // ── TEXT ───────────────────────────────────────────────────────
    public static boolean waitForTextPresent(WebDriver driver, By locator, String text) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    // ── STALENESS ──────────────────────────────────────────────────
    public static boolean waitForStaleness(WebDriver driver, WebElement element) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(ExpectedConditions.stalenessOf(element));
    }

    // ── LIST ───────────────────────────────────────────────────────
    public static List<WebElement> waitForAllVisible(WebDriver driver, By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    // ── SAFE CLICK ─────────────────────────────────────────────────
    public static void safeClick(WebDriver driver, By locator) {
        try {
            waitForClickable(driver, locator).click();
        } catch (ElementClickInterceptedException e) {
            // JS fallback
            WebElement el = driver.findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }

    // ── DISMISS OPTIONAL BANNER ────────────────────────────────────
    public static void dismissCookieBanner(WebDriver driver) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(4))
                    .until(ExpectedConditions.elementToBeClickable(By.id("sp-cc-accept")))
                    .click();
        } catch (TimeoutException ignored) { /* banner not present */ }
    }
}

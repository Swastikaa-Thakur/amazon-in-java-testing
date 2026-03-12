package com.amazon.pages;

import com.amazon.utils.WaitUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page Object for Amazon.in Sign-In page.
 * NOTE: Full login is blocked by CAPTCHA in automated environments.
 * These methods cover UI/form validation only.
 */
public class SignInPage {

    private final WebDriver driver;

    // ── LOCATORS ───────────────────────────────────────────────────
    @FindBy(id = "ap_email")
    private WebElement emailField;

    @FindBy(id = "continue")
    private WebElement continueButton;

    @FindBy(id = "ap_password")
    private WebElement passwordField;

    @FindBy(id = "signInSubmit")
    private WebElement signInSubmit;

    @FindBy(id = "rememberMe")
    private WebElement rememberMeCheckbox;

    @FindBy(css = ".auth-error-message-box, #auth-email-missing-alert, #auth-email-invalid-alert")
    private WebElement errorMessage;

    // ── CONSTRUCTOR ────────────────────────────────────────────────
    public SignInPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // ── NAVIGATION ─────────────────────────────────────────────────
    public void open() {
        driver.get("https://www.amazon.in/ap/signin");
        WaitUtils.waitForVisible(driver, By.id("ap_email"));
    }

    // ── ACTIONS ────────────────────────────────────────────────────
    public void enterEmail(String email) {
        WaitUtils.waitForClickable(driver, emailField).clear();
        emailField.sendKeys(email);
    }

    public void clickContinue() {
        WaitUtils.waitForClickable(driver, continueButton).click();
    }

    public void enterPassword(String password) {
        WaitUtils.waitForVisible(driver, By.id("ap_password")).clear();
        passwordField.sendKeys(password);
    }

    public void clickSignIn() {
        WaitUtils.waitForClickable(driver, signInSubmit).click();
    }

    public void submitEmptyForm() {
        continueButton.click();
    }

    // ── GETTERS ────────────────────────────────────────────────────
    public boolean isEmailFieldVisible() {
        try { return emailField.isDisplayed() && emailField.isEnabled(); }
        catch (NoSuchElementException e) { return false; }
    }

    public boolean isContinueButtonVisible() {
        try { return continueButton.isDisplayed(); }
        catch (NoSuchElementException e) { return false; }
    }

    public boolean isPasswordFieldVisible() {
        try { return passwordField.isDisplayed() && passwordField.isEnabled(); }
        catch (NoSuchElementException e) { return false; }
    }

    public boolean isRememberMePresent() {
        return !driver.findElements(By.id("rememberMe")).isEmpty();
    }

    public boolean isForgotPasswordLinkPresent() {
        return !driver.findElements(By.partialLinkText("Forgot your password")).isEmpty();
    }

    public boolean isCreateAccountLinkPresent() {
        return !driver.findElements(By.partialLinkText("Create your Amazon account")).isEmpty();
    }

    public boolean isErrorDisplayed() {
        try {
            WaitUtils.waitForVisible(driver, By.cssSelector(
                    ".auth-error-message-box, #auth-email-missing-alert, " +
                    "#auth-email-invalid-alert"), 5);
            return true;
        } catch (TimeoutException e) { return false; }
    }

    public boolean isBlockedByCaptcha() {
        return driver.getPageSource().toLowerCase().contains("captcha") ||
               driver.getPageSource().contains("Enter the characters");
    }
}

package com.amazon.tests;

import com.amazon.pages.SignInPage;
import com.amazon.utils.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * TestNG tests for Amazon.in Sign-In page.
 * NOTE: Amazon's CAPTCHA prevents full login automation.
 * These tests cover UI, form validation, and navigation.
 */
public class SignInTest extends BaseTest {

    private SignInPage signInPage;

    @BeforeMethod
    public void setUp() {
        signInPage = new SignInPage(getDriver());
        signInPage.open();
    }

    // ── SIGN-IN FORM ───────────────────────────────────────────────
    @Test(description = "Sign-in page should show email input field")
    public void testEmailFieldVisible() {
        Assert.assertTrue(signInPage.isEmailFieldVisible(),
                "Email / phone number input should be visible");
    }

    @Test(description = "Sign-in page should show Continue button")
    public void testContinueButtonVisible() {
        Assert.assertTrue(signInPage.isContinueButtonVisible(),
                "Continue button should be visible on sign-in page");
    }

    @Test(description = "Submitting empty form should show error")
    public void testEmptyEmailShowsError() {
        signInPage.submitEmptyForm();
        Assert.assertTrue(signInPage.isErrorDisplayed(),
                "Submitting empty email should show a validation error");
    }

    @Test(description = "Submitting invalid email format should show error")
    public void testInvalidEmailShowsError() {
        signInPage.enterEmail("notanemail@@");
        signInPage.clickContinue();
        Assert.assertTrue(signInPage.isErrorDisplayed(),
                "Invalid email format should trigger a validation error");
    }

    @Test(description = "Valid email should reveal password field")
    public void testValidEmailShowsPasswordField() {
        signInPage.enterEmail("testautomation@example.com");
        signInPage.clickContinue();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        Assert.assertTrue(signInPage.isPasswordFieldVisible() || signInPage.isBlockedByCaptcha(),
                "Valid email should show password field (or CAPTCHA in CI)");
    }

    // ── AFTER EMAIL STEP ───────────────────────────────────────────
    @Test(description = "After email step: Forgot Password link should exist")
    public void testForgotPasswordLink() {
        signInPage.enterEmail("test@example.com");
        signInPage.clickContinue();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        if (!signInPage.isBlockedByCaptcha()) {
            Assert.assertTrue(signInPage.isForgotPasswordLinkPresent(),
                    "Forgot Password link should be present");
        } else {
            log.warn("CAPTCHA appeared — skipping Forgot Password assertion");
        }
    }

    @Test(description = "After email step: Keep me signed in checkbox should exist")
    public void testRememberMeCheckbox() {
        signInPage.enterEmail("test@example.com");
        signInPage.clickContinue();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        if (!signInPage.isBlockedByCaptcha()) {
            Assert.assertTrue(signInPage.isRememberMePresent(),
                    "'Keep me signed in' checkbox should be present");
        } else {
            log.warn("CAPTCHA appeared — skipping Remember Me assertion");
        }
    }

    // ── REGISTRATION LINK ──────────────────────────────────────────
    @Test(description = "Create account link should be present on sign-in page")
    public void testCreateAccountLinkPresent() {
        Assert.assertTrue(signInPage.isCreateAccountLinkPresent(),
                "Create your Amazon account link should be present");
    }
}

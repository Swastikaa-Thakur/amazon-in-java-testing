package com.amazon.pages;

import com.amazon.utils.WaitUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

/**
 * Page Object for Amazon.in Shopping Cart page.
 */
public class CartPage {

    private final WebDriver driver;

    // ── LOCATORS ───────────────────────────────────────────────────
    @FindBy(css = ".sc-product-title, [data-name='Active Items'] .a-truncate-full")
    private List<WebElement> cartItemTitles;

    @FindBy(id = "sc-subtotal-amount-activecart")
    private WebElement subtotalAmount;

    @FindBy(css = "input[value='Delete']")
    private List<WebElement> deleteButtons;

    @FindBy(css = "input[value='Save for later']")
    private List<WebElement> saveForLaterButtons;

    @FindBy(css = ".a-button-input[name='proceedToRetailCheckout']")
    private WebElement proceedToBuyButton;

    // ── CONSTRUCTOR ────────────────────────────────────────────────
    public CartPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // ── NAVIGATION ─────────────────────────────────────────────────
    public void open() {
        driver.get("https://www.amazon.in/gp/cart/view.html");
    }

    // ── GETTERS ────────────────────────────────────────────────────
    public boolean isCartEmpty() {
        try {
            String pageText = driver.findElement(By.id("sc-active-cart")).getText();
            return pageText.contains("empty") || pageText.contains("Your Amazon Cart is empty");
        } catch (NoSuchElementException e) {
            return driver.getPageSource().contains("Your Amazon Cart is empty");
        }
    }

    public int getItemCount() {
        return cartItemTitles.size();
    }

    public boolean hasItems() {
        return !cartItemTitles.isEmpty();
    }

    public String getSubtotalText() {
        try { return subtotalAmount.getText().trim(); }
        catch (NoSuchElementException e) { return ""; }
    }

    public boolean isSubtotalInRupees() {
        return getSubtotalText().contains("₹");
    }

    public boolean isProceedToBuyButtonVisible() {
        try { return proceedToBuyButton.isDisplayed(); }
        catch (NoSuchElementException e) { return false; }
    }

    public boolean hasDeleteButtons() {
        return !deleteButtons.isEmpty();
    }

    public boolean hasSaveForLaterButtons() {
        return !saveForLaterButtons.isEmpty();
    }

    // ── ACTIONS ────────────────────────────────────────────────────
    public void deleteFirstItem() {
        if (!deleteButtons.isEmpty()) {
            WaitUtils.waitForClickable(driver, deleteButtons.get(0)).click();
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }
    }

    public void saveFirstItemForLater() {
        if (!saveForLaterButtons.isEmpty()) {
            WaitUtils.waitForClickable(driver, saveForLaterButtons.get(0)).click();
        }
    }

    public void clickProceedToBuy() {
        WaitUtils.waitForClickable(driver, proceedToBuyButton).click();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}

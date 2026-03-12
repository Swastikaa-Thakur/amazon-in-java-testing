package com.amazon.pages;

import com.amazon.utils.WaitUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page Object for Amazon.in Product Detail Page (PDP).
 */
public class ProductDetailPage {

    private final WebDriver driver;

    // ── LOCATORS ───────────────────────────────────────────────────
    @FindBy(id = "productTitle")
    private WebElement productTitle;

    @FindBy(css = ".a-price-symbol")
    private WebElement priceSymbol;

    @FindBy(css = ".a-price-whole")
    private WebElement priceWhole;

    @FindBy(id = "add-to-cart-button")
    private WebElement addToCartButton;

    @FindBy(id = "buy-now-button")
    private WebElement buyNowButton;

    @FindBy(id = "acrPopover")
    private WebElement ratingsPopover;

    @FindBy(id = "acrCustomerReviewText")
    private WebElement reviewCount;

    @FindBy(id = "availability")
    private WebElement availability;

    @FindBy(id = "nav-cart-count")
    private WebElement cartCount;

    @FindBy(id = "landingImage")
    private WebElement mainImage;

    @FindBy(id = "bylineInfo")
    private WebElement bylineInfo;

    @FindBy(id = "feature-bullets")
    private WebElement featureBullets;

    // ── CONSTRUCTOR ────────────────────────────────────────────────
    public ProductDetailPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // ── WAITS ──────────────────────────────────────────────────────
    public void waitForPage() {
        WaitUtils.waitForVisible(driver, By.id("productTitle"));
    }

    // ── GETTERS ────────────────────────────────────────────────────
    public String getProductTitle() {
        return productTitle.getText().trim();
    }

    public boolean isTitleVisible() {
        try { return productTitle.isDisplayed() && !productTitle.getText().isEmpty(); }
        catch (NoSuchElementException e) { return false; }
    }

    public boolean isPriceDisplayedWithRupeeSymbol() {
        try { return priceSymbol.getText().contains("₹"); }
        catch (NoSuchElementException e) {
            // fallback search
            try {
                return driver.findElement(By.cssSelector(".a-price")).getText().contains("₹");
            } catch (NoSuchElementException ex) { return false; }
        }
    }

    public String getPrice() {
        try { return "₹" + priceWhole.getText().trim(); }
        catch (NoSuchElementException e) { return "N/A"; }
    }

    public boolean isAddToCartButtonVisible() {
        try { return addToCartButton.isDisplayed() && addToCartButton.isEnabled(); }
        catch (NoSuchElementException e) { return false; }
    }

    public boolean isBuyNowButtonPresent() {
        return !driver.findElements(By.id("buy-now-button")).isEmpty();
    }

    public boolean isRatingPresent() {
        return !driver.findElements(By.cssSelector(
                "#acrPopover, .a-icon-star, [data-hook='rating-out-of-text']")).isEmpty();
    }

    public boolean isReviewCountPresent() {
        return !driver.findElements(By.id("acrCustomerReviewText")).isEmpty();
    }

    public boolean isPrimeBadgePresent() {
        return !driver.findElements(By.cssSelector(".a-icon-prime")).isEmpty();
    }

    public boolean isMainImageVisible() {
        try { return mainImage.isDisplayed(); }
        catch (NoSuchElementException e) {
            return !driver.findElements(By.cssSelector("#imgTagWrapperId img")).isEmpty();
        }
    }

    public boolean isAvailabilityPresent() {
        return !driver.findElements(By.id("availability")).isEmpty();
    }

    public boolean isDeliveryInfoPresent() {
        return !driver.findElements(By.cssSelector(
                "#mir-layout-DELIVERY_BLOCK, #deliveryBlockMessage, " +
                "[data-feature-name='delivery']")).isEmpty();
    }

    public boolean isFeatureBulletsPresent() {
        return !driver.findElements(By.id("feature-bullets")).isEmpty();
    }

    public boolean isBylineInfoPresent() {
        return !driver.findElements(By.id("bylineInfo")).isEmpty();
    }

    // ── ACTIONS ────────────────────────────────────────────────────
    public void clickAddToCart() {
        WaitUtils.waitForClickable(driver, addToCartButton).click();
    }

    public boolean isAddToCartConfirmationVisible() {
        try {
            WaitUtils.waitForVisible(driver, By.cssSelector(
                    "#attachDisplayAddBaseAlert, #attachDisplayAddBasePopover, " +
                    "#huc-v2-order-row-confirm-text, .a-alert-success"), 8);
            return true;
        } catch (TimeoutException e) { return false; }
    }

    public int getCartCountAfterAdd() {
        try { return Integer.parseInt(cartCount.getText().trim()); }
        catch (Exception e) { return 0; }
    }

    public void scrollToReviews() {
        try {
            WebElement reviews = driver.findElement(By.id("customer-reviews-content"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", reviews);
        } catch (NoSuchElementException ignored) {}
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public boolean urlMatchesProductPattern() {
        return getCurrentUrl().matches(".*\\/dp\\/[A-Z0-9]{10}.*");
    }
}

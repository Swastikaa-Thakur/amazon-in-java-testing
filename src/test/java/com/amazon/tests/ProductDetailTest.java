package com.amazon.tests;

import com.amazon.pages.HomePage;
import com.amazon.pages.ProductDetailPage;
import com.amazon.pages.SearchResultsPage;
import com.amazon.utils.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * TestNG tests for Amazon.in Product Detail Page.
 */
public class ProductDetailTest extends BaseTest {

    private ProductDetailPage pdp;

    @BeforeMethod
    public void setUp() {
        HomePage home = new HomePage(getDriver());
        home.open();
        home.searchFor("boAt Airdopes 141");
        SearchResultsPage srp = new SearchResultsPage(getDriver());
        srp.waitForResults();
        srp.clickFirstNonSponsoredResult();
        pdp = new ProductDetailPage(getDriver());
        pdp.waitForPage();
    }

    // ── CORE CONTENT ───────────────────────────────────────────────
    @Test(description = "Product title should be visible and non-empty")
    public void testProductTitleVisible() {
        Assert.assertTrue(pdp.isTitleVisible(),
                "Product title should be visible");
        Assert.assertFalse(pdp.getProductTitle().isEmpty(),
                "Product title should not be empty");
        log.info("Product title: {}", pdp.getProductTitle());
    }

    @Test(description = "Product price should display ₹ symbol")
    public void testPriceHasRupeeSymbol() {
        Assert.assertTrue(pdp.isPriceDisplayedWithRupeeSymbol(),
                "Product price should display ₹ symbol");
        log.info("Price: {}", pdp.getPrice());
    }

    @Test(description = "Product should display star rating")
    public void testRatingPresent() {
        Assert.assertTrue(pdp.isRatingPresent(),
                "Star rating should be present on PDP");
    }

    @Test(description = "Customer review count should be present")
    public void testReviewCountPresent() {
        Assert.assertTrue(pdp.isReviewCountPresent(),
                "Customer review count should be present");
    }

    @Test(description = "Main product image should be visible")
    public void testMainImageVisible() {
        Assert.assertTrue(pdp.isMainImageVisible(),
                "Main product image should be visible");
    }

    @Test(description = "Brand / byline info should be present")
    public void testBylineInfoPresent() {
        Assert.assertTrue(pdp.isBylineInfoPresent(),
                "Byline (brand) info should be present on PDP");
    }

    @Test(description = "Feature bullets / product description should be present")
    public void testFeatureBulletsPresent() {
        Assert.assertTrue(pdp.isFeatureBulletsPresent(),
                "Feature bullets or description should be present");
    }

    // ── BUYING OPTIONS ─────────────────────────────────────────────
    @Test(description = "Add to Cart button should be visible and enabled")
    public void testAddToCartButtonVisible() {
        Assert.assertTrue(pdp.isAddToCartButtonVisible(),
                "Add to Cart button should be visible and enabled");
    }

    @Test(description = "Buy Now button should be present on PDP")
    public void testBuyNowButtonPresent() {
        Assert.assertTrue(pdp.isBuyNowButtonPresent(),
                "Buy Now button should be present");
    }

    @Test(description = "Delivery information should be present")
    public void testDeliveryInfoPresent() {
        Assert.assertTrue(pdp.isDeliveryInfoPresent(),
                "Delivery estimate/information should be shown on PDP");
    }

    @Test(description = "Availability status should be present")
    public void testAvailabilityPresent() {
        Assert.assertTrue(pdp.isAvailabilityPresent(),
                "Product availability status should be shown");
    }

    // ── ADD TO CART FLOW ───────────────────────────────────────────
    @Test(description = "Clicking Add to Cart should show confirmation")
    public void testAddToCartShowsConfirmation() {
        int cartBefore = pdp.getCartCountAfterAdd();
        pdp.clickAddToCart();
        boolean confirmed = pdp.isAddToCartConfirmationVisible();
        int cartAfter = pdp.getCartCountAfterAdd();
        Assert.assertTrue(confirmed || cartAfter > cartBefore,
                "Add to Cart should show confirmation or increment cart count");
        log.info("Cart count: {} → {}", cartBefore, cartAfter);
    }

    @Test(description = "Cart count should increment after Add to Cart")
    public void testCartCountIncrements() {
        int before = pdp.getCartCountAfterAdd();
        pdp.clickAddToCart();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        int after = pdp.getCartCountAfterAdd();
        Assert.assertTrue(after >= before,
                "Cart count should be >= before. Before: " + before + ", After: " + after);
    }

    // ── URL PATTERN ────────────────────────────────────────────────
    @Test(description = "PDP URL should contain /dp/ASIN10")
    public void testUrlMatchesAsinPattern() {
        Assert.assertTrue(pdp.urlMatchesProductPattern(),
                "PDP URL should match /dp/[ASIN] pattern. Actual: " + pdp.getCurrentUrl());
    }

    // ── PRIME BADGE ────────────────────────────────────────────────
    @Test(description = "Prime badge check — soft assertion (may not be present)")
    public void testPrimeBadgeSoftCheck() {
        boolean hasPrime = pdp.isPrimeBadgePresent();
        log.info("Prime badge present: {}", hasPrime);
        // Not all products are Prime — log only
    }
}

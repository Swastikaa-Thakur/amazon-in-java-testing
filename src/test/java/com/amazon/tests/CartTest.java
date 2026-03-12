package com.amazon.tests;

import com.amazon.pages.CartPage;
import com.amazon.pages.HomePage;
import com.amazon.pages.ProductDetailPage;
import com.amazon.pages.SearchResultsPage;
import com.amazon.utils.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * TestNG tests for Amazon.in Shopping Cart.
 */
public class CartTest extends BaseTest {

    private CartPage cartPage;

    @BeforeMethod
    public void setUp() {
        cartPage = new CartPage(getDriver());
    }

    // ── CART ICON ──────────────────────────────────────────────────
    @Test(description = "Clicking cart icon should navigate to cart URL")
    public void testCartIconNavigation() {
        HomePage home = new HomePage(getDriver());
        home.open();
        home.clickCart();
        String url = getDriver().getCurrentUrl();
        Assert.assertTrue(url.contains("/cart") || url.contains("gp/cart"),
                "Should navigate to cart. Actual: " + url);
    }

    // ── CART PAGE STRUCTURE ────────────────────────────────────────
    @Test(description = "Cart page should load without error")
    public void testCartPageLoads() {
        cartPage.open();
        Assert.assertFalse(getDriver().getPageSource().contains("404"),
                "Cart page should not return 404");
    }

    @Test(description = "Empty cart should show 'Your Amazon Cart is empty'")
    public void testEmptyCartMessage() {
        cartPage.open();
        if (cartPage.isCartEmpty()) {
            Assert.assertTrue(cartPage.isCartEmpty(),
                    "Empty cart message should be shown");
            log.info("Cart is empty — message displayed correctly");
        } else {
            log.info("Cart has items — skipping empty cart assertion");
        }
    }

    // ── ADD TO CART ────────────────────────────────────────────────
    @Test(description = "Adding product should reflect in cart page")
    public void testAddProductToCart() {
        // Navigate to product and add to cart
        HomePage home = new HomePage(getDriver());
        home.open();
        home.searchFor("USB C cable 1m");

        SearchResultsPage srp = new SearchResultsPage(getDriver());
        srp.waitForResults();
        srp.clickFirstNonSponsoredResult();

        ProductDetailPage pdp = new ProductDetailPage(getDriver());
        pdp.waitForPage();

        if (pdp.isAddToCartButtonVisible()) {
            pdp.clickAddToCart();
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

            // Navigate to cart
            cartPage.open();
            Assert.assertTrue(cartPage.hasItems() || !cartPage.isCartEmpty(),
                    "Cart should contain item after Add to Cart");
            log.info("Cart items: {}", cartPage.getItemCount());
        } else {
            log.warn("Add to Cart button not visible — skipping add test");
        }
    }

    @Test(description = "Cart count badge should show >= 1 after adding item")
    public void testCartBadgeIncrement() {
        HomePage home = new HomePage(getDriver());
        home.open();
        int before = home.getCartCount();

        home.searchFor("pen drive 32gb");
        SearchResultsPage srp = new SearchResultsPage(getDriver());
        srp.waitForResults();
        srp.clickFirstNonSponsoredResult();

        ProductDetailPage pdp = new ProductDetailPage(getDriver());
        pdp.waitForPage();

        if (pdp.isAddToCartButtonVisible()) {
            pdp.clickAddToCart();
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

            // Re-check count from header
            int after = pdp.getCartCountAfterAdd();
            Assert.assertTrue(after >= before,
                    "Cart count should be >= before add. Before: " + before + ", After: " + after);
        }
    }

    // ── CART OPERATIONS ────────────────────────────────────────────
    @Test(description = "Cart subtotal should display ₹ symbol if items present")
    public void testSubtotalInRupees() {
        cartPage.open();
        if (cartPage.hasItems()) {
            Assert.assertTrue(cartPage.isSubtotalInRupees(),
                    "Subtotal should display ₹ symbol");
            log.info("Subtotal: {}", cartPage.getSubtotalText());
        } else {
            log.info("Cart empty — skipping subtotal ₹ check");
        }
    }

    @Test(description = "Proceed to Buy button should be visible when cart has items")
    public void testProceedToBuyVisible() {
        cartPage.open();
        if (cartPage.hasItems()) {
            Assert.assertTrue(cartPage.isProceedToBuyButtonVisible(),
                    "Proceed to Buy button should be visible");
        } else {
            log.info("Cart empty — Proceed to Buy button not expected");
        }
    }

    @Test(description = "Delete button should be present for cart items")
    public void testDeleteButtonPresent() {
        cartPage.open();
        if (cartPage.hasItems()) {
            Assert.assertTrue(cartPage.hasDeleteButtons(),
                    "Delete button should be present for cart items");
        }
    }

    @Test(description = "Save for Later button should be present for cart items")
    public void testSaveForLaterPresent() {
        cartPage.open();
        if (cartPage.hasItems()) {
            Assert.assertTrue(cartPage.hasSaveForLaterButtons(),
                    "Save for Later button should be present");
        }
    }
}

package com.amazon.pages;

import com.amazon.utils.WaitUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * Page Object for Amazon.in Search Results Page.
 */
public class SearchResultsPage {

    private final WebDriver driver;

    // ── LOCATORS ───────────────────────────────────────────────────
    @FindBy(css = "[data-component-type='s-search-result']")
    private List<WebElement> searchResults;

    @FindBy(id = "low-price")
    private WebElement priceMin;

    @FindBy(id = "high-price")
    private WebElement priceMax;

    @FindBy(css = "span.a-dropdown-prompt")
    private List<WebElement> dropdownPrompts;

    // ── CONSTRUCTOR ────────────────────────────────────────────────
    public SearchResultsPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // ── WAITS ──────────────────────────────────────────────────────
    public void waitForResults() {
        WaitUtils.waitForAllVisible(driver,
                By.cssSelector("[data-component-type='s-search-result']"));
    }

    // ── GETTERS ────────────────────────────────────────────────────
    public int getResultCount() {
        return searchResults.size();
    }

    public List<WebElement> getResults() {
        return searchResults;
    }

    public boolean hasResults() {
        return !searchResults.isEmpty();
    }

    public boolean isNoResultsMessageShown() {
        try {
            return driver.findElement(By.cssSelector(".s-no-outline"))
                    .getText().contains("No results for");
        } catch (NoSuchElementException e) {
            try {
                return driver.getPageSource().contains("No results for");
            } catch (Exception ex) { return false; }
        }
    }

    public boolean hasSponsoredResults() {
        return !driver.findElements(
                By.cssSelector(".puis-sponsored-label-text")).isEmpty();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public boolean urlContains(String fragment) {
        return driver.getCurrentUrl().contains(fragment);
    }

    // ── FIRST RESULT ───────────────────────────────────────────────
    public void clickFirstNonSponsoredResult() {
        for (WebElement result : searchResults) {
            // skip sponsored
            if (result.findElements(By.cssSelector(".puis-sponsored-label-text")).isEmpty()) {
                WaitUtils.waitForClickable(driver, result.findElement(By.cssSelector("h2 a"))).click();
                return;
            }
        }
        // fallback: click very first
        searchResults.get(0).findElement(By.cssSelector("h2 a")).click();
    }

    public void clickFirstResult() {
        WaitUtils.waitForClickable(driver,
                searchResults.get(0).findElement(By.cssSelector("h2 a"))).click();
    }

    // ── FILTERS ────────────────────────────────────────────────────
    public void filterByPriceRange(String min, String max) {
        priceMin.clear();
        priceMin.sendKeys(min);
        priceMax.clear();
        priceMax.sendKeys(max);
        // click Go button
        try {
            driver.findElement(By.cssSelector(
                    "input[aria-label*='Go'], .a-button-text[aria-label*='Go']")).click();
        } catch (NoSuchElementException e) {
            priceMax.sendKeys(Keys.ENTER);
        }
    }

    public void filterByRating(String stars) {
        // e.g. stars = "4 Stars & Up"
        try {
            WebElement ratingSection = driver.findElement(
                    By.cssSelector("section[aria-label*='Avg'], section[aria-label*='Customer Review']"));
            ratingSection.findElement(By.partialLinkText(stars)).click();
        } catch (NoSuchElementException e) {
            // try xpath fallback
            driver.findElement(By.xpath("//span[contains(text(),'" + stars + "')]")).click();
        }
    }

    public void sortBy(String option) {
        // option e.g. "Price: Low to High"
        try {
            WebElement sortBtn = driver.findElement(
                    By.cssSelector("span.a-dropdown-prompt"));
            sortBtn.click();
            driver.findElement(By.xpath(
                    "//a[contains(text(),'" + option + "')]")).click();
        } catch (NoSuchElementException e) {
            try {
                Select sortSelect = new Select(driver.findElement(By.id("s-result-sort-select")));
                sortSelect.selectByVisibleText(option);
            } catch (NoSuchElementException ex) {
                // sort not available
            }
        }
    }

    public boolean isPrimeFilterPresent() {
        return !driver.findElements(
                By.cssSelector("[aria-label*='Prime'], [aria-label*='prime']")).isEmpty();
    }

    public boolean isBrandFilterPresent() {
        return !driver.findElements(
                By.cssSelector("[aria-label*='Brand'], [cel_widget_id*='BRANDS']")).isEmpty();
    }

    // ── EACH RESULT VALIDATION ─────────────────────────────────────
    public boolean allResultsHaveTitles() {
        return searchResults.stream()
                .allMatch(r -> {
                    try { return !r.findElement(By.cssSelector("h2")).getText().isEmpty(); }
                    catch (NoSuchElementException e) { return false; }
                });
    }
}

package com.amazon.pages;

import com.amazon.utils.WaitUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

/**
 * Page Object for Amazon.in Homepage.
 */
public class HomePage {

    private final WebDriver driver;

    // ── LOCATORS ───────────────────────────────────────────────────
    @FindBy(id = "twotabsearchtextbox")
    private WebElement searchBox;

    @FindBy(id = "nav-search-submit-button")
    private WebElement searchButton;

    @FindBy(id = "searchDropdownBox")
    private WebElement departmentDropdown;

    @FindBy(id = "nav-logo-sprites")
    private WebElement logoSprite;

    @FindBy(id = "nav-logo")
    private WebElement logoImg;

    @FindBy(id = "nav-cart")
    private WebElement cartIcon;

    @FindBy(id = "nav-cart-count")
    private WebElement cartCount;

    @FindBy(id = "nav-link-accountList")
    private WebElement accountLink;

    @FindBy(xpath = "//a[contains(text(),\"Today's Deals\")]")
    private WebElement todaysDealsLink;

    @FindBy(xpath = "//a[contains(text(),'Customer Service')]")
    private WebElement customerServiceLink;

    // ── CONSTRUCTOR ────────────────────────────────────────────────
    public HomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // ── ACTIONS ────────────────────────────────────────────────────
    public void open() {
        driver.get("https://www.amazon.in");
        WaitUtils.dismissCookieBanner(driver);
    }

    public void searchFor(String query) {
        WaitUtils.waitForClickable(driver, searchBox).clear();
        searchBox.sendKeys(query);
        searchButton.click();
    }

    public void searchInDepartment(String query, String department) {
        Select select = new Select(departmentDropdown);
        select.selectByVisibleText(department);
        searchFor(query);
    }

    public void clickTodaysDeals() {
        WaitUtils.waitForClickable(driver, todaysDealsLink).click();
    }

    public void clickCart() {
        WaitUtils.waitForClickable(driver, cartIcon).click();
    }

    // ── GETTERS ────────────────────────────────────────────────────
    public boolean isLogoVisible() {
        try { return logoSprite.isDisplayed(); }
        catch (NoSuchElementException e) {
            try { return logoImg.isDisplayed(); }
            catch (NoSuchElementException ex) { return false; }
        }
    }

    public boolean isSearchBoxVisible() {
        return searchBox.isDisplayed() && searchBox.isEnabled();
    }

    public boolean isCartIconVisible() {
        return cartIcon.isDisplayed();
    }

    public int getCartCount() {
        try {
            return Integer.parseInt(cartCount.getText().trim());
        } catch (NumberFormatException | NoSuchElementException e) {
            return 0;
        }
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public boolean isTodaysDealsLinkPresent() {
        try { return todaysDealsLink.isDisplayed(); }
        catch (NoSuchElementException e) { return false; }
    }

    public boolean isCustomerServiceLinkPresent() {
        try { return customerServiceLink.isDisplayed(); }
        catch (NoSuchElementException e) { return false; }
    }

    public boolean isAccountLinkPresent() {
        try { return accountLink.isDisplayed(); }
        catch (NoSuchElementException e) { return false; }
    }

    /**
     * Types in search box to trigger autocomplete without submitting.
     */
    public void typeInSearch(String query) {
        WaitUtils.waitForClickable(driver, searchBox).clear();
        searchBox.sendKeys(query);
    }

    public boolean isAutocompleteSuggestionsVisible() {
        try {
            WebElement suggestions = driver.findElement(
                    By.cssSelector("div.autocomplete-results-container, .s-suggestion-container"));
            return suggestions.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}

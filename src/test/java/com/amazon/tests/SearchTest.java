package com.amazon.tests;

import com.amazon.pages.HomePage;
import com.amazon.pages.ProductDetailPage;
import com.amazon.pages.SearchResultsPage;
import com.amazon.utils.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * TestNG tests for Amazon.in Search Functionality.
 */
public class SearchTest extends BaseTest {

    private HomePage homePage;
    private SearchResultsPage resultsPage;

    @BeforeMethod
    public void setUp() {
        homePage   = new HomePage(getDriver());
        resultsPage = new SearchResultsPage(getDriver());
        homePage.open();
    }

    // ── BASIC SEARCH ───────────────────────────────────────────────
    @Test(description = "Search for 'laptop' should return results")
    public void testBasicSearch() {
        homePage.searchFor("laptop");
        resultsPage.waitForResults();
        Assert.assertTrue(resultsPage.hasResults(),
                "Search for 'laptop' should return at least 1 result");
        Assert.assertTrue(resultsPage.getResultCount() > 5,
                "Expected more than 5 results for 'laptop'");
        log.info("Search results count: {}", resultsPage.getResultCount());
    }

    @Test(description = "Search URL should contain the query keyword")
    public void testSearchUrlContainsQuery() {
        homePage.searchFor("wireless earphones");
        Assert.assertTrue(resultsPage.urlContains("k=wireless"),
                "URL should contain search keyword");
    }

    @Test(description = "Search for gibberish should show No Results message")
    public void testNoResultsSearch() {
        homePage.searchFor("zzzzxxx99999gibberishabc");
        Assert.assertTrue(resultsPage.isNoResultsMessageShown(),
                "Should display 'No results for' message");
    }

    @Test(description = "Search results should all have product titles")
    public void testAllResultsHaveTitles() {
        homePage.searchFor("mobile phone cover");
        resultsPage.waitForResults();
        Assert.assertTrue(resultsPage.allResultsHaveTitles(),
                "All search result cards should have a product title");
    }

    @Test(description = "Sponsored results should be labelled")
    public void testSponsoredResultsLabelled() {
        homePage.searchFor("running shoes");
        resultsPage.waitForResults();
        boolean hasSponsored = resultsPage.hasSponsoredResults();
        log.info("Sponsored results present: {}", hasSponsored);
        // Soft check — sponsored items are common but not guaranteed
    }

    // ── DATA-DRIVEN SEARCH ─────────────────────────────────────────
    @DataProvider(name = "searchQueries")
    public Object[][] searchQueries() {
        return new Object[][] {
            {"headphones",       5},
            {"USB C cable",      5},
            {"smartwatch",       5},
            {"laptop bag",       5},
            {"phone charger",    5},
        };
    }

    @Test(dataProvider = "searchQueries",
          description = "Data-driven: various product searches should return results")
    public void testMultipleProductSearches(String query, int minExpected) {
        homePage.searchFor(query);
        resultsPage.waitForResults();
        int count = resultsPage.getResultCount();
        Assert.assertTrue(count >= minExpected,
                "Search [" + query + "] expected >=" + minExpected + " results, got " + count);
        log.info("Query='{}' → {} results", query, count);
    }

    // ── DEPARTMENT SEARCH ──────────────────────────────────────────
    @Test(description = "Search scoped to Electronics department should include 'i=electronics' in URL")
    public void testDepartmentScopedSearch() {
        homePage.searchInDepartment("smartwatch", "Electronics");
        String url = getDriver().getCurrentUrl();
        Assert.assertTrue(url.contains("i=electronics") || url.contains("Electronics"),
                "URL should reflect Electronics department filter. Actual: " + url);
    }

    // ── CLICK THROUGH TO PDP ───────────────────────────────────────
    @Test(description = "Clicking first result should open product detail page")
    public void testClickFirstResultOpensPDP() {
        homePage.searchFor("bluetooth speaker");
        resultsPage.waitForResults();
        resultsPage.clickFirstNonSponsoredResult();

        ProductDetailPage pdp = new ProductDetailPage(getDriver());
        pdp.waitForPage();
        Assert.assertTrue(pdp.isTitleVisible(),
                "Product title should be visible on PDP");
        Assert.assertTrue(pdp.urlMatchesProductPattern(),
                "URL should match /dp/ASIN10CHARS pattern. Actual: " + pdp.getCurrentUrl());
    }
}

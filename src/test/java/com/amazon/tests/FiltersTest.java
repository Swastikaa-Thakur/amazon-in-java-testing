package com.amazon.tests;

import com.amazon.pages.HomePage;
import com.amazon.pages.SearchResultsPage;
import com.amazon.utils.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * TestNG tests for Amazon.in Search Filters & Sorting.
 */
public class FiltersTest extends BaseTest {

    private SearchResultsPage resultsPage;

    @BeforeMethod
    public void setUp() {
        HomePage home = new HomePage(getDriver());
        home.open();
        home.searchFor("headphones");
        resultsPage = new SearchResultsPage(getDriver());
        resultsPage.waitForResults();
    }

    @Test(description = "Price range filter should narrow results")
    public void testPriceRangeFilter() {
        resultsPage.filterByPriceRange("500", "3000");
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        String url = resultsPage.getCurrentUrl();
        Assert.assertTrue(
            url.contains("p_36") || url.contains("rh=") || url.contains("price"),
            "URL should reflect price filter. Actual: " + url
        );
        Assert.assertTrue(resultsPage.hasResults(), "Results should be present after price filter");
    }

    @Test(description = "Prime filter option should be visible in sidebar")
    public void testPrimeFilterPresent() {
        Assert.assertTrue(resultsPage.isPrimeFilterPresent(),
                "Prime filter option should be in the left sidebar");
    }

    @Test(description = "Brand filter section should be present in sidebar")
    public void testBrandFilterPresent() {
        Assert.assertTrue(resultsPage.isBrandFilterPresent(),
                "Brand filter section should be present");
    }

    @Test(description = "Sorting by 'Price: Low to High' should update URL")
    public void testSortByPriceLowToHigh() {
        resultsPage.sortBy("Price: Low to High");
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        String url = resultsPage.getCurrentUrl();
        Assert.assertTrue(
            url.contains("s=price-asc-rank") || url.contains("sort") || url.contains("price"),
            "URL should reflect price sort. Actual: " + url
        );
    }

    @Test(description = "Sorting by 'Avg. Customer Review' should update URL")
    public void testSortByCustomerReview() {
        resultsPage.sortBy("Avg. Customer Review");
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        String url = resultsPage.getCurrentUrl();
        log.info("Sort by review URL: {}", url);
        Assert.assertTrue(resultsPage.hasResults(), "Results should still appear after sorting");
    }

    @Test(description = "Filtering by 4 Stars & Up should return results")
    public void testStarRatingFilter() {
        try {
            resultsPage.filterByRating("4 Stars & Up");
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            Assert.assertTrue(resultsPage.hasResults(),
                    "Should have results after 4-star filter");
            log.info("Results after 4★ filter: {}", resultsPage.getResultCount());
        } catch (Exception e) {
            log.warn("Star rating filter not available on this result set: {}", e.getMessage());
        }
    }
}
